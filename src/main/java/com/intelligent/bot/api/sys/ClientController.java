package com.intelligent.bot.api.sys;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.annotate.AvoidRepeatRequest;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.dao.MjTaskDao;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.*;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.req.sys.*;
import com.intelligent.bot.model.res.sys.*;
import com.intelligent.bot.service.sys.*;
import com.intelligent.bot.utils.sys.ImgUtil;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/client")
@Log4j2
@Transactional(rollbackFor = E.class)
public class ClientController {

    @Resource
    IUserService userService;
    @Resource
    IMessageLogService useLogService;
    @Resource
    IAnnouncementService announcementService;
    @Resource
    IProductService productService;
    @Resource
    IOrderService orderService;
    @Resource
    MjTaskDao mjTaskDao;
    @Resource
    IMjTaskService mjTaskService;
    @Resource
    IGptRoleService gptRoleService;


    @RequestMapping(value = "/home", name = "用户首页信息")
    public B<ClientHomeRes> home(@Validated @RequestBody ClientHomeReq req) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        User user = userService.getById(JwtUtil.getUserId());
        if(!user.getAvatar().contains("http")){
            user.setAvatar(sysConfig.getImgReturnUrl() + user.getAvatar());
        }
        List<MessageLog> logList;
        if(!Objects.equals(req.getSendType(), SendType.GPT.getType())){
            logList = useLogService.lambdaQuery()
                    .select(MessageLog::getUseValue,MessageLog::getId,MessageLog::getSendType)
                    .eq(MessageLog::getUserId, JwtUtil.getUserId())
                    .eq(MessageLog::getSendType, req.getSendType())
                    .orderByDesc(MessageLog::getId)
                    .list();
        } else {
            logList = useLogService.lambdaQuery()
                    .select(MessageLog::getUseValue,MessageLog::getId,MessageLog::getSendType)
                    .eq(MessageLog::getUserId, JwtUtil.getUserId())
                    .in(MessageLog::getSendType, SendType.GPT.getType(),SendType.GPT_4.getType())
                    .orderByDesc(MessageLog::getId)
                    .list();
        }
        List<Announcement> list = announcementService.lambdaQuery().select(Announcement::getContent).orderByDesc(Announcement::getSort).last("limit 1").list();
        ClientHomeRes clientHomeRes = BeanUtil.copyProperties(user, ClientHomeRes.class);
        clientHomeRes.setAnnouncement((null != list && list.size() > 0) ? list.get(0).getContent() : "暂无通知公告");
        if(Objects.equals(req.getSendType(), SendType.MJ.getType())){
            List<MjTaskRes> mjTaskList = mjTaskDao.selectUserMjTask(JwtUtil.getUserId());
            List<MjTaskTransformRes> transformList = mjTaskDao.selectTransform();
            for (MjTaskRes mjTaskRes : mjTaskList) {
                List<MjTaskTransformRes> taskTransformList = new ArrayList<>();
                for (MjTaskTransformRes transform : transformList) {
                    if(transform.getRelatedTaskId().equals(mjTaskRes.getId())){
                        taskTransformList.add(transform);
                    }
                }
                if(null !=  mjTaskRes.getImageUrl() && mjTaskRes.getImageUrl().contains(".jpg")){
                    mjTaskRes.setImageUrl(sysConfig.getImgReturnUrl() + mjTaskRes.getImageUrl());
                }
                mjTaskRes.setTaskTransformList(taskTransformList);
            }
            clientHomeRes.setMjTaskList(mjTaskList);
            return B.okBuild(clientHomeRes);

        }else {
            List<ClientHomeLogRes> homeLogResList = new ArrayList<>();
            logList.forEach(e -> {
                ClientHomeLogRes res = new ClientHomeLogRes();
                res.setId(e.getId());
                res.setSendType(e.getSendType());
                if (e.getSendType().equals(SendType.GPT.getType())
                        || e.getSendType().equals(SendType.GPT_4.getType())
                        || e.getSendType().equals(SendType.BING.getType())) {
                    res.setTitle(JSONObject.parseArray(e.getUseValue(), Message.class).get(0).getContent());
                    res.setContent(e.getUseValue());
                } else {
                    MessageLogSave messageLogSave = JSONObject.parseObject(e.getUseValue(), MessageLogSave.class);
                    List<String> imgList = new ArrayList<>();
                    messageLogSave.getImgList().forEach( m ->{
                        imgList.add(sysConfig.getImgReturnUrl() + m );
                    });
                    res.setTitle(messageLogSave.getPrompt());
                    messageLogSave.setImgList(imgList);
                    res.setContent(JSONObject.toJSONString(messageLogSave));
                }
                homeLogResList.add(res);

            });
            clientHomeRes.setLogList(homeLogResList);
        }
        clientHomeRes.setGptRoleList(gptRoleService.getGptRoleLimit10());
        return B.okBuild(clientHomeRes);
    }

    @RequestMapping(value = "/updateAvatar", name = "修改头像")
    @AvoidRepeatRequest(intervalTime = 2626560, msg = "头像每个月可更换一次")
    public B<Void> updateAvatar(MultipartFile file) throws IOException {
        User user = new User();
        String oldFileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
        String fileName = ImgUtil.uploadMultipartFile(file, oldFileName);
        user.setAvatar(fileName);
        user.setId(JwtUtil.getUserId());
        userService.updateById(user);
        return B.okBuild();
    }

    @RequestMapping(value = "/updatePassword", name = "修改密码")
    @AvoidRepeatRequest(intervalTime = 2626560, msg = "密码每个月可更换一次")
    public B<String> updatePassword(@Validated @RequestBody ClientUpdatePasswordReq req) {
        User user = new User();
        user.setId(JwtUtil.getUserId());
        user.setPassword(SecureUtil.md5(req.getPassword()));
        userService.updateById(user);
        RedisUtil.deleteObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + user.getId());
        return B.okBuild("修改成功请重新登陆");
    }

    @RequestMapping(value = "/recharge", name = "充值")
    public B<ClientRechargeRes> recharge() {
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        return B.okBuild(ClientRechargeRes.builder()
                .productList(productService.getProductList())
                .orderList(orderService.userOrderList(JwtUtil.getUserId()))
                .payType(payConfig.getPayType())
                .build());
    }

    @RequestMapping(value = "/register/method",name = "查询注册方式", method = RequestMethod.POST)
    public B<Integer> getRegisterMethod() {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        return B.okBuild(sysConfig.getRegistrationMethod());
    }

    @RequestMapping(value = "/getFunctionState",name = "获取配置开启状态", method = RequestMethod.POST)
    public B<GetFunctionState> getOpenSdState() {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        return B.okBuild( GetFunctionState.builder()
                .isOpenSd(sysConfig.getIsOpenSd())
                .isOpenFlagStudio(sysConfig.getIsOpenFlagStudio())
                .isOpenBing(sysConfig.getIsOpenBing())
                .isOpenMj(sysConfig.getIsOpenMj()).build()
        );
    }

    @RequestMapping(value = "/delete/log", name = "删除对话")
    public B<Void> deleteLog(@Validated @RequestBody ClientDeleteLog req) {
        useLogService.removeById(req.getId());
        return B.okBuild();
    }

    @RequestMapping(value = "/empty/log", name = "清空对话")
    public B<Void> emptyLog(@Validated @RequestBody ClientEmptyLog req) {
        useLogService.lambdaUpdate()
                .set(MessageLog::getDeleted,1)
                .eq(MessageLog::getUserId,JwtUtil.getUserId())
                .eq(MessageLog::getSendType,req.getSendType())
                .update();
        return B.okBuild();
    }

    @RequestMapping(value = "/delete/mj/task", name = "删除mj任务")
    public B<Void> deleteMjTask(@Validated @RequestBody ClientDeleteLog req) {
        mjTaskService.deleteMjTask(req.getId());
        return B.okBuild();
    }

    @RequestMapping(value = "/empty/mj/task", name = "清空mj任务")
    public B<Void> emptyMjTask() {
        mjTaskService.emptyMjTask(JwtUtil.getUserId());
        return B.okBuild();
    }

    @RequestMapping(value = "/upload/img", name = "上传图片")
    @AvoidRepeatRequest(intervalTime = 10, msg = "请勿频繁上传图片")
    public B<String> uploadImg(MultipartFile file) throws IOException {
        String oldFileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
        String fileName = ImgUtil.uploadMultipartFile(file, oldFileName);
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        return B.okBuild(cacheObject.getImgReturnUrl() + fileName);
    }

    @RequestMapping(value = "/client/conf", name = "获取客户端配置，logo，名称")
    public B<ClientConfRes> clientConf(){
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        ClientConfRes clientConfRes = BeanUtil.copyProperties(cacheObject, ClientConfRes.class);
        clientConfRes.setClientLogo(cacheObject.getImgReturnUrl() + clientConfRes.getClientLogo());
        return B.okBuild(clientConfRes);
    }
}
