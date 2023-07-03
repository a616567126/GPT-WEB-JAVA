package com.intelligent.bot.service.mj.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.req.mj.MjCallBack;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.sys.IUserService;
import com.intelligent.bot.utils.sys.FileUtil;
import com.intelligent.bot.utils.sys.PicUtils;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Resource
	IUserService userService;
	@Resource
	WxMpService wxMpService;

	@Override
	public void notifyTaskChange(Task task) {
		String notifyHook = task.getNotifyHook();
		if (CharSequenceUtil.isBlank(notifyHook)) {
			return;
		}
		try {
			String paramsStr = OBJECT_MAPPER.writeValueAsString(task);
//			log.info("任务变更, 触发推送, task: {}", paramsStr);
			postJson(notifyHook, paramsStr);
		} catch (Exception e) {
			log.warn("回调通知接口失败: {}", e.getMessage());
		}
	}

	private void postJson(String notifyHook, String paramsJson) throws WxErrorException {
		MjCallBack mjTask = JSONObject.parseObject(paramsJson, MjCallBack.class);
		log.info("mj开始回调,回调内容：{}", mjTask);
		SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String fileLocalPath = null ;
		if(null != mjTask.getImageUrl()){
			fileLocalPath = mjTask.getImageUrl();
			mjTask.setImageUrl(FileUtil.imageUrlToBase64(cacheObject.getImgReturnUrl() + mjTask.getImageUrl()));
		}
		if(mjTask.getSubType() == 1){
			SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
		}else {
			if(mjTask.getStatus().equals(TaskStatus.SUCCESS)){
				if(null != mjTask.getImageUrl()){
					User user = userService.getById(mjTask.getUserId());
					String content;
					if(mjTask.getAction().equals(TaskAction.UPSCALE)){
						content = "\uD83C\uDFA8绘图完成\n\n"+
								"\uD83D\uDECE\uFE0F任务id："+mjTask.getId()+"\n"+
								"\uD83E\uDD73本次消耗次数："+CommonConst.MJ_NUMBER;
					}else {
						content = "\uD83C\uDFA8绘图完成\n" +
								"\uD83E\uDD73本次消耗次数："+CommonConst.MJ_NUMBER+"\n"+
								"\uD83D\uDECE\uFE0F任务id："+mjTask.getId()+"\n"+
								"\uD83D\uDCAC咒语："+mjTask.getPrompt()+"\n"+
								"\uD83D\uDDEF译文："+mjTask.getPromptEn()+"\n"+
								"\uD83D\uDD0D\uFE0E放大命令：/U +空格+ 图片位置1.2.3.4 + 空格 +任务id\n"
								+"例如放大图片1：/U 1 "+mjTask.getId()+"\n\n"+
								"\uD83D\uDCAB变换命令：/V +空格+ 图片位置1.2.3.4 + 空格 +任务id\n"
								+"例如变换图片1：/V 1 "+mjTask.getId();
					}
					String fileUrl = cacheObject.getImgUploadUrl() + fileLocalPath;
					PicUtils.commpressPicForScale(fileUrl,fileUrl);
					File file = new File(fileUrl);
					WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
					WxMpKefuMessage message = WxMpKefuMessage.IMAGE().toUser(user.getFromUserName()).mediaId(wxMediaUploadResult.getMediaId()).build();
					wxMpService.getKefuService().sendKefuMessage(message);
					message=WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(content).build();
					wxMpService.getKefuService().sendKefuMessage(message);
				}
			}
		}

	}
}
