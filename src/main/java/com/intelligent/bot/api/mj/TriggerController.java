package com.intelligent.bot.api.mj;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.api.mj.support.TaskHelper;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.Action;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.mj.*;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.model.res.mj.TriggerSubmitRes;
import com.intelligent.bot.model.res.mj.UVDataRes;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.service.mj.DiscordService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.service.sys.IMessageLogService;
import com.intelligent.bot.utils.mj.ConvertUtils;
import com.intelligent.bot.utils.sys.DateUtil;
import com.intelligent.bot.utils.sys.FileUtil;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/trigger")
@Log4j2
@Transactional(rollbackFor = E.class)
public class TriggerController {

    @Resource
    DiscordService discordService;
    @Resource
    BaiDuService baiDuService;
    @Resource
    TaskHelper taskHelper;
    @Resource
    CheckService checkService;
    @Resource
    AsyncService asyncService;
    @Resource
    IMessageLogService useLogService;


    @PostMapping("/submit")
    public synchronized B<TriggerSubmitRes> submit(@RequestBody SubmitReq submitReq) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if(null == sysConfig.getIsOpenMj() || sysConfig.getIsOpenMj() == 0){
            throw new E("暂未开启Mj");
        }
        Long logId = checkService.checkUser(MessageLog.builder()
                .useNumber(CommonConst.MJ_NUMBER)
                .sendType(SendType.MJ.getType())
                .useValue(submitReq.getPrompt())
                .userId(JwtUtil.getUserId()).build());
        if (submitReq.getAction() == null) {
            throw new E("action 不能为空");
        }
        if(submitReq.getPrompt().contains("nsfw")){
            throw new E("生成内容不合规");
        }
        if ((submitReq.getAction() == Action.UPSCALE || submitReq.getAction() == Action.VARIATION)
                && (submitReq.getIndex() < 1 || submitReq.getIndex() > 4)) {
            throw new E("校验错误");
        }
        if(!baiDuService.textToExamine(submitReq.getPrompt())){
            throw new E("生成内容不合规");
        }
        Task task = new Task();
        task.setNotifyHook(sysConfig.getApiUrl() + CommonConst.MJ_CALL_BACK_URL);
        task.setId(RandomUtil.randomNumbers(16));
        task.setSubmitTime(System.currentTimeMillis());
        task.setState(String.valueOf(logId));
        task.setAction(submitReq.getAction());
        String key;
        B<Void> result;
        String promptEn;
        if (Action.IMAGINE.equals(submitReq.getAction())) {
            String prompt = submitReq.getPrompt();
            if (CharSequenceUtil.isBlank(prompt)) {
                throw new E("prompt 不能为空");
            }
            key = task.getId();
            task.setPrompt(prompt);
            promptEn = Validator.isChinese(prompt) ? prompt : this.baiDuService.translateToEnglish(prompt).trim();
            task.setFinalPrompt("[" + task.getId() + "]" + promptEn);
            task.setDescription("/imagine " + submitReq.getPrompt());
            this.taskHelper.putTask(task.getId(), task);
            result = this.discordService.imagine(task.getFinalPrompt());
        } else {
            if (CharSequenceUtil.isBlank(submitReq.getTaskId())) {
                throw new E("taskId 不能为空");
            }
            Task targetTask = this.taskHelper.findById(submitReq.getTaskId());
            if (targetTask == null) {
                throw new E("任务不存在或已失效");
            }
            if (!TaskStatus.SUCCESS.equals(targetTask.getStatus())) {
                throw new E("关联任务状态错误");
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
                throw new E("暂不支持 reset 操作");
            }
        }
        if (result.getStatus() != ResultEnum.SUCCESS.getCode()) {
            this.taskHelper.removeTask(key);
            throw new E("请求失败" + result.getStatus());
        }
        return B.okBuild(TriggerSubmitRes.builder().taskId(task.getId()).promptEn(promptEn).build());
    }

    @PostMapping("/submit/uv")
    public synchronized B<TriggerSubmitRes> submitUV(@RequestBody UVSubmitReq uvsubmitReq) {
        UVDataRes uvData = ConvertUtils.convertUVData(uvsubmitReq.getContent());
        if (uvData == null) {
            throw new E("/up 参数错误");
        }
        SubmitReq submitReq = new SubmitReq();
        submitReq.setAction(uvData.getAction());
        submitReq.setTaskId(uvData.getId());
        submitReq.setIndex(uvData.getIndex());
        return submit(submitReq);
    }

    @PostMapping("/list")
    public B<List<Task>> listTask() {
        return B.okBuild(this.taskHelper.listTask());
    }

    @PostMapping("getTask")
    public B<Task> getTask(@RequestBody TaskReq req) {
        return B.okBuild(this.taskHelper.findById(req.getTaskId()));
    }

    @PostMapping("callBack")
    public void callBack(@RequestBody MjCallBack mjCallBack) throws Exception {
        log.info("mj开始回调,回调内容：{}", mjCallBack);
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String localImgUrl = FileUtil.base64ToImage(FileUtil.imageUrlToBase64(mjCallBack.getImageUrl()));
        mjCallBack.setImageUrl(cacheObject.getImgReturnUrl() + localImgUrl);
        MessageLog messageLog = useLogService.getById(Long.valueOf(mjCallBack.getState()));
        SseEmitterServer.sendMessage(Long.valueOf(mjCallBack.getState()),JSONObject.toJSONString(mjCallBack));
        MessageLogSave messageLogSave = MessageLogSave.builder()
                .prompt(mjCallBack.getPrompt())
                .type(SendType.MJ.getRemark())
                .startTime(DateUtil.timestamp2LocalDateTime(mjCallBack.getSubmitTime()))
                .imgList(Collections.singletonList(localImgUrl)).build();
        asyncService.updateLog(messageLog.getId(),messageLogSave);
    }
}
