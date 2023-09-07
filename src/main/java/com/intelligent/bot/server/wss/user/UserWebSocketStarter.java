package com.intelligent.bot.server.wss.user;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.model.mj.doman.ReturnCode;
import com.intelligent.bot.server.wss.WebSocketStarter;
import com.intelligent.bot.utils.mj.AsyncLockUtils;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.DataType;
import net.dv8tion.jda.internal.requests.WebSocketCode;
import net.dv8tion.jda.internal.utils.compress.Decompressor;
import net.dv8tion.jda.internal.utils.compress.ZlibDecompressor;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class UserWebSocketStarter extends WebSocketAdapter implements WebSocketStarter {
	private static final int CONNECT_RETRY_LIMIT = 3;

	private final DiscordAccount account;
	private final UserMessageListener userMessageListener;
	private final ScheduledExecutorService heartExecutor;
	private final String wssServer;
	private final DataObject authData;

	private Decompressor decompressor;
	private WebSocket socket = null;
	private String resumeGatewayUrl;
	private String sessionId;

	private Future<?> heartbeatInterval;
	private Future<?> heartbeatTimeout;
	private boolean heartbeatAck = false;
	private Object sequence = null;
	private long interval = 41250;
	private boolean trying = false;
	public UserWebSocketStarter(String wssServer, DiscordAccount account, UserMessageListener userMessageListener) {
		this.wssServer = wssServer;
		this.account = account;
		this.userMessageListener = userMessageListener;
		this.heartExecutor = Executors.newSingleThreadScheduledExecutor();
		this.authData = createAuthData();
	}

	@Override
	public void setTrying(boolean trying) {
		this.trying = trying;
	}

	@Override
	public synchronized void start() throws Exception {
		this.decompressor = new ZlibDecompressor(2048);
		WebSocketFactory webSocketFactory = createWebSocketFactory();
		String gatewayUrl = CharSequenceUtil.isNotBlank(this.resumeGatewayUrl) ? this.resumeGatewayUrl : this.wssServer;
		this.socket = webSocketFactory.createSocket(gatewayUrl + "/?encoding=json&v=9&compress=zlib-stream");
		this.socket.addListener(this);
		this.socket.addHeader("Accept-Encoding", "gzip, deflate, br")
				.addHeader("Accept-Language", "zh-CN,zh;q=0.9")
				.addHeader("Cache-Control", "no-cache")
				.addHeader("Pragma", "no-cache")
				.addHeader("Connection", "Upgrade")
				.addHeader("Upgrade", "websocket")
				.addHeader("Host", "gateway.discord.gg")
				.addHeader("Origin", "https://discord.com")
				.addHeader("Sec-Websocket-Extensions", "permessage-deflate; client_max_window_bits")
				.addHeader("User-Agent", CommonConst.USERAGENT);
		this.socket.connect();
	}

	@Override
	public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
		log.debug("[wss-{}] Connected to websocket.", this.account.getDisplay());
	}

	@Override
	public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
		log.error("[wss-{}] There was some websocket error.", this.account.getDisplay(), cause);
	}

	@Override
	public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
		int code;
		String closeReason;
		if (closedByServer) {
			code = serverCloseFrame.getCloseCode();
			closeReason = serverCloseFrame.getCloseReason();
		} else {
			code = clientCloseFrame.getCloseCode();
			closeReason = clientCloseFrame.getCloseReason();
		}
		connectFinish(code, closeReason);
		if (this.trying) {
			return;
		}
		if (code == 5240) {
			// 隐式关闭wss
			clearAllStates();
		} else if (code >= 4000) {
			log.warn("[wss-{}] Can't reconnect! Account disabled. Closed by {}({}).", this.account.getDisplay(), code, closeReason);
			clearAllStates();
			this.account.setState(0);
		} else if (code == 2001) {
			// reconnect
			log.warn("[wss-{}] Waiting try reconnect...", this.account.getDisplay());
			tryReconnect();
		} else {
			log.warn("[wss-{}] Closed by {}({}). Waiting try new connection...", this.account.getDisplay(), code, closeReason);
			tryNewConnect();
		}
	}

	private void tryReconnect() {
		clearSocketStates();
		try {
			this.trying = true;
			tryStart(true);
		} catch (Exception e) {
			if (e instanceof TimeoutException) {
				sendClose(5240, "try new connect");
			}
			log.warn("[wss-{}] Try reconnect fail: {}, Waiting try new connection...", this.account.getDisplay(), e.getMessage());
			ThreadUtil.sleep(1000);
			tryNewConnect();
		}
	}

	private void tryNewConnect() {
		this.trying = true;
		for (int i = 1; i <= CONNECT_RETRY_LIMIT; i++) {
			clearAllStates();
			try {
				tryStart(false);
				return;
			} catch (Exception e) {
				if (e instanceof TimeoutException) {
					sendClose(5240, "try new connect");
				}
				log.warn("[wss-{}] Try new connection fail ({}): {}", this.account.getDisplay(), i, e.getMessage());
				ThreadUtil.sleep(5000);
			}
		}
		log.error("[wss-{}] Account disabled", this.account.getDisplay());
		this.account.setState(0);
	}

	public void tryStart(boolean reconnect) throws Exception {
		start();
		AsyncLockUtils.LockObject lock = AsyncLockUtils.waitForLock("wss:" + this.account.getChannelId(), Duration.ofSeconds(20));
		int code = null == lock.getCode() ? 0 : lock.getCode();
		if (code == ReturnCode.SUCCESS) {
			log.debug("[wss-{}] {} success.", this.account.getDisplay(), reconnect ? "Reconnect" : "New connect");
			return;
		}
		throw new ValidateException(lock.getDescription());
	}

	@Override
	public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
		if (this.decompressor == null) {
			return;
		}
		byte[] decompressBinary = this.decompressor.decompress(binary);
		if (decompressBinary == null) {
			return;
		}
		String json = new String(decompressBinary, StandardCharsets.UTF_8);
		DataObject data = DataObject.fromJson(json);
		int opCode = data.getInt("op");
		switch (opCode) {
			case WebSocketCode.HEARTBEAT:
				log.debug("[wss-{}] Receive heartbeat.", this.account.getDisplay());
				handleHeartbeat();
				break;
			case WebSocketCode.HEARTBEAT_ACK :
				this.heartbeatAck = true;
				clearHeartbeatTimeout();
				break;
			case WebSocketCode.HELLO :
				handleHello(data);
				doResumeOrIdentify();
				break;
			case WebSocketCode.RESUME :
				log.debug("[wss-{}] Receive resumed.", this.account.getDisplay());
				connectSuccess();
				break;
			case WebSocketCode.RECONNECT :
				sendReconnect("receive server reconnect");
				break;
			case WebSocketCode.INVALIDATE_SESSION :
				sendClose(1009, "receive session invalid");
				break;
			case WebSocketCode.DISPATCH :
				handleDispatch(data);
				break;
			default :
				log.debug("[wss-{}] Receive unknown code: {}.", this.account.getDisplay(), data);
		}
	}

	private void handleHello(DataObject data) {
		clearHeartbeatInterval();
		this.interval = data.getObject("d").getLong("heartbeat_interval");
		this.heartbeatAck = true;
		this.heartbeatInterval = this.heartExecutor.scheduleAtFixedRate(() -> {
			if (this.heartbeatAck) {
				this.heartbeatAck = false;
				send(WebSocketCode.HEARTBEAT, this.sequence);
			} else {
				sendReconnect("heartbeat has not ack interval");
			}
		}, (long) Math.floor(RandomUtil.randomDouble(0, 1) * this.interval), this.interval, TimeUnit.MILLISECONDS);
	}

	private void doResumeOrIdentify() {
		if (CharSequenceUtil.isBlank(this.sessionId)) {
			log.debug("[wss-{}] Send identify msg.", this.account.getDisplay());
			send(WebSocketCode.IDENTIFY, this.authData);
		} else {
			log.debug("[wss-{}] Send resume msg.", this.account.getDisplay());
			send(WebSocketCode.RESUME, DataObject.empty().put("token", this.account.getUserToken())
					.put("session_id", this.sessionId).put("seq", this.sequence));
		}
	}

	private void handleHeartbeat() {
		send(WebSocketCode.HEARTBEAT, this.sequence);
		this.heartbeatTimeout = ThreadUtil.execAsync(() -> {
			ThreadUtil.sleep(this.interval);
			sendReconnect("heartbeat has not ack");
		});
	}

	private void clearAllStates() {
		clearSocketStates();
		clearResumeStates();
	}

	private void clearSocketStates() {
		clearHeartbeatTimeout();
		clearHeartbeatInterval();
		this.socket = null;
		this.decompressor = null;
	}

	private void clearResumeStates() {
		this.sessionId = null;
		this.sequence = null;
		this.resumeGatewayUrl = null;
	}

	private void clearHeartbeatInterval() {
		if (this.heartbeatInterval != null) {
			this.heartbeatInterval.cancel(true);
			this.heartbeatInterval = null;
		}
	}

	private void clearHeartbeatTimeout() {
		if (this.heartbeatTimeout != null) {
			this.heartbeatTimeout.cancel(true);
			this.heartbeatTimeout = null;
		}
	}

	private void sendReconnect(String reason) {
		sendClose(2001, reason);
	}

	private void sendClose(int code, String reason) {
		if (this.socket != null) {
			this.socket.sendClose(code, reason);
		}
	}

	private void send(int op, Object d) {
		if (this.socket != null) {
			this.socket.sendText(DataObject.empty().put("op", op).put("d", d).toString());
		}
	}

	private void connectSuccess() {
		this.trying = false;
		connectFinish(ReturnCode.SUCCESS, "");
	}

	private void connectFinish(int code, String description) {
		AsyncLockUtils.LockObject lock = AsyncLockUtils.getLock("wss:" + this.account.getChannelId());
		if (lock != null) {
			lock.setCode( code);
			lock.setDescription(description);
			lock.awake();
		}
	}

	private void handleDispatch(DataObject raw) {
		this.sequence = raw.opt("s").orElse(null);
		if (!raw.isType("d", DataType.OBJECT)) {
			return;
		}
		DataObject content = raw.getObject("d");
		String t = raw.getString("t", null);
		if ("READY".equals(t)) {
			this.sessionId = content.getString("session_id");
			this.resumeGatewayUrl = content.getString("resume_gateway_url");
			log.debug("[wss-{}] Dispatch ready: identify.", this.account.getDisplay());
			connectSuccess();
			return;
		} else if ("RESUMED".equals(t)) {
			log.debug("[wss-{}] Dispatch read: resumed.", this.account.getDisplay());
			connectSuccess();
			return;
		}
		try {
			this.userMessageListener.onMessage(raw);
		} catch (Exception e) {
			log.error("[wss-{}] Handle message error", this.account.getDisplay(), e);
		}
	}

	private DataObject createAuthData() {
		UserAgent agent = UserAgent.parseUserAgentString(CommonConst.USERAGENT);
		DataObject connectionProperties = DataObject.empty()
				.put("browser", agent.getBrowser().getGroup().getName())
				.put("browser_user_agent", CommonConst.USERAGENT)
				.put("browser_version", agent.getBrowserVersion().toString())
				.put("client_build_number", 222963)
				.put("client_event_source", null)
				.put("device", "")
				.put("os", agent.getOperatingSystem().getName())
				.put("referer", "https://www.midjourney.com")
				.put("referrer_current", "")
				.put("referring_domain", "www.midjourney.com")
				.put("referring_domain_current", "")
				.put("release_channel", "stable")
				.put("system_locale", "zh-CN");
		DataObject presence = DataObject.empty()
				.put("activities", DataArray.empty())
				.put("afk", false)
				.put("since", 0)
				.put("status", "online");
		DataObject clientState = DataObject.empty()
				.put("api_code_version", 0)
				.put("guild_versions", DataObject.empty())
				.put("highest_last_message_id", "0")
				.put("private_channels_version", "0")
				.put("read_state_version", 0)
				.put("user_guild_settings_version", -1)
				.put("user_settings_version", -1);
		return DataObject.empty()
				.put("capabilities", 16381)
				.put("client_state", clientState)
				.put("compress", false)
				.put("presence", presence)
				.put("properties", connectionProperties)
				.put("token", this.account.getUserToken());
	}
}
