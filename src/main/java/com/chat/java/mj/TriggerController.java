package com.chat.java.mj;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.controller.sd.SdController;
import com.chat.java.exception.CustomException;
import com.chat.java.flow.server.ChatWebSocketServer;
import com.chat.java.mj.enums.Action;
import com.chat.java.mj.enums.TaskStatus;
import com.chat.java.mj.req.MjCallBack;
import com.chat.java.mj.req.SubmitReq;
import com.chat.java.mj.req.TaskReq;
import com.chat.java.mj.req.UVSubmitReq;
import com.chat.java.mj.res.TriggerSubmit;
import com.chat.java.mj.result.Message;
import com.chat.java.mj.service.DiscordService;
import com.chat.java.mj.service.TranslateService;
import com.chat.java.mj.support.Task;
import com.chat.java.mj.support.TaskHelper;
import com.chat.java.mj.util.ConvertUtils;
import com.chat.java.mj.util.UVData;
import com.chat.java.model.SysConfig;
import com.chat.java.utils.FileUtil;
import com.chat.java.utils.JwtUtil;
import com.chat.java.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trigger")
@RequiredArgsConstructor
@Log4j2
public class TriggerController {


    private final DiscordService discordService;

    private final TranslateService translateService;

    private final TaskHelper taskHelper;

    @PostMapping("/submit")
    public B<TriggerSubmit> submit(@RequestBody SubmitReq submitReq) {
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        if(null == cacheObject.getIsOpenMj() || cacheObject.getIsOpenMj() == 0){
            throw new CustomException("暂未开启mj");
        }
        if (submitReq.getAction() == null) {
            throw new CustomException("action 不能为空");
        }
        if ((submitReq.getAction() == Action.UPSCALE || submitReq.getAction() == Action.VARIATION)
                && (submitReq.getIndex() < 1 || submitReq.getIndex() > 4)) {
            throw new CustomException("校验错误");
        }
        if(!translateService.textToExamine(submitReq.getPrompt())){
            throw new CustomException("生成内容不合规");
        }
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        Task task = new Task();
        task.setNotifyHook(submitReq.getNotifyHook() == null ? sysConfig.getMjNotifyHook() : submitReq.getNotifyHook());
        task.setId(RandomUtil.randomNumbers(16));
        task.setSubmitTime(System.currentTimeMillis());
        task.setState(String.valueOf(JwtUtil.getUserId()));
        task.setAction(submitReq.getAction());
        String key;
        Message<Void> result;
        String promptEn;
        if (Action.IMAGINE.equals(submitReq.getAction())) {
            String prompt = submitReq.getPrompt();
            if (!StringUtils.isEmpty(prompt)) {
                throw new CustomException("prompt 不能为空");
            }
            key = task.getId();
            task.setPrompt(prompt);
            promptEn = Validator.isChinese(StrUtil.removeAllLineBreaks(prompt)) ? prompt : this.translateService.translateToEnglish(prompt).trim();
            task.setFinalPrompt("[" + task.getId() + "]" + promptEn);
            task.setDescription("/imagine " + submitReq.getPrompt());
            this.taskHelper.putTask(task.getId(), task);
            result = this.discordService.imagine(task.getFinalPrompt());
        } else {
            if (CharSequenceUtil.isBlank(submitReq.getTaskId())) {
                throw new CustomException("taskId 不能为空");
            }
            Task targetTask = this.taskHelper.findById(submitReq.getTaskId());
            if (targetTask == null) {
                throw new CustomException("任务不存在或已失效");
            }
            if (!TaskStatus.SUCCESS.equals(targetTask.getStatus())) {
                throw new CustomException("关联任务状态错误");
            }
            promptEn = targetTask.getPrompt();
            task.setPrompt(targetTask.getPrompt());
            task.setFinalPrompt(targetTask.getFinalPrompt());
            task.setRelatedTaskId(ConvertUtils.findTaskIdByFinalPrompt(targetTask.getFinalPrompt()));
            key = targetTask.getMessageId() + "-" + submitReq.getAction();
            this.taskHelper.putTask(key, task);
            if (Action.UPSCALE.equals(submitReq.getAction())) {
                task.setDescription("/up " + submitReq.getTaskId() + " U" + submitReq.getIndex());
                result = this.discordService.upscale(targetTask.getMessageId(), submitReq.getIndex(), targetTask.getMessageHash());
            } else if (Action.VARIATION.equals(submitReq.getAction())) {
                task.setDescription("/up " + submitReq.getTaskId() + " V" + submitReq.getIndex());
                result = this.discordService.variation(targetTask.getMessageId(), submitReq.getIndex(), targetTask.getMessageHash());
            } else {
                // todo 暂不支持 reset, 接收mj消息时, 无法找到对应task
                throw new CustomException("暂不支持 reset 操作");
            }
        }
        if (result.getCode() != Message.SUCCESS_CODE) {
            this.taskHelper.removeTask(key);
            throw new CustomException("请求失败" + result.getCode());
        }
        return B.okBuild(TriggerSubmit.builder().taskId(task.getId()).promptEn(promptEn).build());
    }

    @PostMapping("/submit/uv")
    public B<TriggerSubmit> submitUV(@RequestBody UVSubmitReq uvsubmitReq) {
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        if(null == cacheObject.getIsOpenMj() || cacheObject.getIsOpenMj() == 0){
            throw new CustomException("暂未开启mj");
        }
        UVData uvData = ConvertUtils.convertUVData(uvsubmitReq.getContent());
        if (uvData == null) {
            throw new CustomException("/up 参数错误");
        }
        SubmitReq submitReq = new SubmitReq();
        submitReq.setAction(uvData.getAction());
        submitReq.setTaskId(uvData.getId());
        submitReq.setIndex(uvData.getIndex());
        submitReq.setNotifyHook(uvsubmitReq.getNotifyHook());
        return submit(submitReq);
    }

    @PostMapping("/list")
    public B<List<Task>> listTask() {
        return B.okBuild(this.taskHelper.listTask());
    }

    @PostMapping("getTask")
    public B<Task> getTask(@RequestBody TaskReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        if(null == cacheObject.getIsOpenMj() || cacheObject.getIsOpenMj() == 0){
            throw new CustomException("暂未开启mj");
        }
        return B.okBuild(this.taskHelper.findById(req.getTaskId()));
    }

    @PostMapping("callBack")
    public void callBack(@RequestBody MjCallBack mjCallBack) throws Exception {
        log.info(("mj开始回调"));
        log.info("回调内容：{}",mjCallBack);
        SysConfig cacheObject = RedisUtil.getCacheObject("sysConfig");
        mjCallBack.setImageUrl(cacheObject.getImgReturnUrl()+ SdController.base64ToImage(FileUtil.imageUrlToBase64(mjCallBack.getImageUrl())));
        ChatWebSocketServer.sendInfo(JSONObject.toJSONString(mjCallBack),Long.valueOf(mjCallBack.getState()));
    }
    @PostMapping("testSend")
    public void testSend() throws Exception {
        ChatWebSocketServer.sendInfo(JSONObject.toJSONString("你好啊"),1652143528215601153L);
    }
}
