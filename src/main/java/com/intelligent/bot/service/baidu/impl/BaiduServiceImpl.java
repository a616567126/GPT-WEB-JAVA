package com.intelligent.bot.service.baidu.impl;


import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class BaiduServiceImpl implements BaiDuService {
	private static final String TRANSLATE_API = "https://fanyi-api.baidu.com/api/trans/vip/translate";
	private static final String BAIDU_TOKEN_API = "https://aip.baidubce.com/oauth/2.0/token";
	private static final String BAIDU_TO_EXAMINE_API = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined?access_token=";



	@Override
	public String translateToEnglish(String prompt) {
		if (!containsChinese(prompt)) {
			return prompt;
		}
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String salt = RandomUtil.randomNumbers(5);
		String sign = MD5.create().digestHex(sysConfig.getBaiduAppid() + prompt + salt + sysConfig.getBaiduSecret());
		String url = TRANSLATE_API + "?from=zh&to=en&appid=" + sysConfig.getBaiduAppid() + "&salt=" + salt + "&q=" + prompt + "&sign=" + sign;
		try {
			ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(url, String.class);
			if (responseEntity.getStatusCode() != HttpStatus.OK || CharSequenceUtil.isBlank(responseEntity.getBody())) {
				throw new E(responseEntity.getStatusCodeValue() + " - " + responseEntity.getBody());
			}
			JSONObject result = JSONObject.parseObject(responseEntity.getBody());
			if (responseEntity.getBody().contains("error_code")) {
				throw new E(result.getString("error_code") + " - " + result.getString("error_msg"));
			}
			return result.getJSONArray("trans_result").getJSONObject(0).getString("dst");
		} catch (Exception e) {
			log.warn("调用百度翻译失败: {}", e.getMessage());
		}
		return prompt;
	}


	@Override
	public boolean textToExamine(String prompt) {
		String body = HttpUtil.createPost(BAIDU_TO_EXAMINE_API +"?access_token="+ getAccessToken()+"&text="+prompt)
				.header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
				.header(Header.ACCEPT, ContentType.JSON.getValue())
				.execute()
				.body();
		return !body.contains("不合规");
	}

	/**
	 * 从用户的AK，SK生成鉴权签名（Access Token）
	 * @return 鉴权签名（Access Token）
	 * @throws IOException IO异常
	 */
	static String getAccessToken()  {
		String accessToken = RedisUtil.getCacheObject(CommonConst.BAIDU_TO_EXAMINE_REDIS_KEY);
		if(StringUtils.isEmpty(accessToken)){
			SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
			String body = HttpUtil.createPost(BAIDU_TOKEN_API)
					.header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
					.header(Header.ACCEPT, ContentType.JSON.getValue())
					.form("client_id",sysConfig.getBaiduKey())
					.form("client_secret",sysConfig.getBaiduSecretKey())
					.form("grant_type","client_credentials")
					.execute()
					.body();
			accessToken = JSONObject.parseObject(body).getString("access_token");
			RedisUtil.setCacheObject(CommonConst.BAIDU_TO_EXAMINE_REDIS_KEY,accessToken,29L, TimeUnit.DAYS);
		}
		return accessToken;
	}

	boolean containsChinese(String prompt) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(prompt);
		return m.find();
	}

}
