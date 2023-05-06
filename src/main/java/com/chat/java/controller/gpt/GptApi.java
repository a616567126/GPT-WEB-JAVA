package com.chat.java.controller.gpt;

import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.model.*;
import com.chat.java.model.gptdto.GptAlphaDto;
import com.chat.java.model.gptdto.GptCreditGrantsDto;
import com.chat.java.model.gptdto.GptTurboDto;
import com.chat.java.service.*;
import com.chat.java.utils.InitUtil;
import com.chat.java.utils.JwtUtil;
import com.chat.java.utils.ProxyUtil;
import com.chat.java.utils.WebClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;



@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Log4j2
@Api(tags = {"gpt相关接口"})
public final class GptApi {

    /**
     * broker tool
     */
    private final ProxyUtil proxyUtil;

    @Resource
    IGptKeyService gptKeyService;
    @Resource
    IUserService userService;
    @Resource
    IRefuelingKitService refuelingKitService;
    @Resource
    IUseLogService useLogService;
    @Resource
    AsyncLogService asyncLogService;




    /**
     * 正常对话（上下文非流式，流式暂时使用socket实现）
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/chat/turbo", name = "GPT-Turbo", produces = MediaType.APPLICATION_JSON_VALUE)
    public B gptTurbo(@Validated @RequestBody final GptTurboDto dto) {
//        final List<GptTurboModel.Messages> messages = dto.getMessages();
        /*
         * Obtain user-sent data for word interception
         * last one is always the data sent by  user
         */
//        weChatDetectUtils.filtration(messages.get(messages.size() - 1).getContent(), dto.getOpenId());
        // switch to the OpenAPI model
        final GptTurboModel gptTurboModel = GptTurboDto.convertToGptTurboModel(dto);
        final String mainKey = InitUtil.getMainKey();
        B result = checkUser(dto.getType(), mainKey, JSONObject.toJSONString(gptTurboModel.getMessages()),dto.getLogId());
        if(result.getStatus() != 20000){
            return B.buildGptErr(result.getMessage());
        }
        return B.buildGptData( WebClientUtil.build(proxyUtil.getProxy(), "/v1/chat/completions", gptTurboModel, mainKey,(Long)result.getData()));

    }


    /**
     * 画图
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/chat/official", name = "GPT-official", produces = MediaType.APPLICATION_JSON_VALUE)
    public B gptAlpha(@Validated @RequestBody final GptAlphaDto dto) {
        //filter sensitive words ( WeChat )
//        weChatDetectUtils.filtration(dto.getPrompt(), dto.getOpenId());
        // to DTO
        final GptAlphaModel gptAlphaModel = GptAlphaDto.convertToGptAlphaModel(dto);
        // get  key at random
        final String mainKey = InitUtil.getMainKey();
        B result = checkUser(dto.getType(), mainKey, gptAlphaModel.getPrompt(),dto.getLogId());
        if(result.getStatus() != 20000){
            return B.buildGptErr(result.getMessage());
        }
        return B.buildGptData(
                WebClientUtil.build(proxyUtil.getProxy(), "/v1/images/generations", gptAlphaModel, mainKey,(Long)result.getData())
        );
    }

    /**
     * 余额查询1.0已废弃！！！
     * @param dto
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/creditGrants",produces = MediaType.APPLICATION_JSON_VALUE)
    public B creditGrants(@Validated @RequestBody final GptCreditGrantsDto dto) throws JsonProcessingException {
//        HttpResponse response = HttpRequest
//                .get("https://api.openai.com/dashboard/billing/credit_grants")
//                .header("Authorization", "Bearer " + dto.getKey())
//                .execute();
//        String body = response.body();
//        if (!response.isOk()) {
//            if (response.getStatus() == CommonError.OPENAI_AUTHENTICATION_ERROR.code()
//                    || response.getStatus() == CommonError.OPENAI_LIMIT_ERROR.code()
//                    || response.getStatus() == CommonError.OPENAI_SERVER_ERROR.code()) {
//                OpenAiResponse openAiResponse = JSONUtil.toBean(response.body(), OpenAiResponse.class);
//                log.error(openAiResponse.getError().getMessage());
//                return Result.error(openAiResponse.getError().getMessage());
//            }
//            String errorMsg = response.body();
//            log.error("询余额请求异常：{}", errorMsg);
//            OpenAiResponse openAiResponse = JSONUtil.toBean(errorMsg, OpenAiResponse.class);
//            if (Objects.nonNull(openAiResponse.getError())) {
//                log.error(openAiResponse.getError().getMessage());
//                return Result.error(openAiResponse.getError().getMessage());
//            }
//            return Result.error(CommonError.RETRY_ERROR.msg());
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        // 读取Json 返回值
//        CreditGrantsResponse completionResponse = mapper.readValue(body, CreditGrantsResponse.class);
        return B.buildGptData(null);

    }

    /**
     * 余额查询2.0与之前版本返回内容完全不一致(查询100天之内的)
     * @param dto
     * @return
     */
    @PostMapping(value = "/billing/usage",produces = MediaType.APPLICATION_JSON_VALUE)
    public B billingUsage(@Validated @RequestBody final GptCreditGrantsDto dto) throws JsonProcessingException {
        return B.buildGptData(
                WebClientUtil.build(
                        "/v1/dashboard/billing/usage?start_date="
                                +LocalDate.now().plusDays(-99)
                                +"&end_date="+LocalDate.now()
                        , dto.getKey()));
    }

    public B checkUser(Integer type, String mainKey,String message,Long logId){
        List<GptKey> gptKeyList = gptKeyService.lambdaQuery().eq(GptKey::getKey, mainKey).last("limit 1").list();
        if(null == gptKeyList || gptKeyList.size() == 0){
            return B.buildGptErr("Key 异常 请稍后重试");
        }
        //查询当前用户信息
        UseLog useLog = new UseLog();
        useLog.setGptKey(mainKey);
        useLog.setUseValue(message);
        useLog.setUserId(JwtUtil.getUserId());
        User user = userService.getById(JwtUtil.getUserId());
        if(type != 2){
            if(type == 0){
                //判断剩余次数
                if(user.getRemainingTimes() < 1){
                    return B.buildGptErr("剩余次数不足请充值");
                }
                useLog.setUseType(1);
                user.setRemainingTimes(user.getRemainingTimes() - 1);
            }
            if(type == 1){
                //月卡用户 先查询是否有可用的加油包
                Long userKitId = refuelingKitService.getUserKitId(user.getId());
                if(userKitId > 0){
                    useLog.setKitId(userKitId);
                    useLog.setUseType(3);
                }else {
                    //判断月卡是否到期
                    if(user.getExpirationTime().compareTo(LocalDateTime.now()) < 0){
                        //次数用户 查询用户次数
                        if(user.getRemainingTimes() < 1){
                            return B.buildGptErr("月卡过期或当日已超过最大访问次数");
                        }
                        useLog.setUseType(1);
                        user.setRemainingTimes(user.getRemainingTimes() - 1);
                    }else {
                        //是否已达今日已达上线
                        Integer dayUseNumber = useLogService.getDayUseNumber(user.getId());
                        if((dayUseNumber + 1) > user.getCardDayMaxNumber()){
                            //判断剩余次数
                            if(user.getRemainingTimes() < 1){
                                return B.buildGptErr("月卡过期或当日已超过最大访问次数");
                            }
                            useLog.setUseType(1);
                            user.setRemainingTimes(user.getRemainingTimes() - 1);
                        }else {
                            useLog.setUseType(2);
                        }
                    }
                }
            }
        }
        GptKey gptKey = gptKeyList.get(0);
        gptKey.setUseNumber(gptKey.getUseNumber()+1);
        gptKey.setOperateTime(LocalDateTime.now());
        asyncLogService.saveKeyLog(gptKey,user);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        if(methodName.equals("gptTurbo")){
            useLogService.save(useLog);
            if(null != logId){
                useLogService.removeById(logId);
            }
        }
        return B.buildGptData(useLog.getId());
    }


}
