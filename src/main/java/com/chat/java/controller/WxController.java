package com.chat.java.controller;

import com.chat.java.service.WxService;
import com.chat.java.utils.RedisUtil;
import com.chat.java.utils.WeixinCheckoutUtil;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;

/**
 * ClassName:WxController
 * Package:com.chat.java.controller
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/31 - 16:15
 * @Version: v1.0
 */
@RestController
@RequestMapping(value = "/wx",produces = "application/json; charset=UTF-8")
@Log4j2
public class WxController {

    @Resource
    WxService wxService;

    @Autowired
    private WxMpService wxMpService;

    @Resource
    RedisUtil redisUtil;


    @RequestMapping(value = "/callBack", method = RequestMethod.GET)
    public String token(String signature,String timestamp,String nonce,String echostr) {
        log.error("wxtoken校验");
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (signature != null && WeixinCheckoutUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return null;
    }

    @RequestMapping(value = "/callBack", method = RequestMethod.POST)
    public void message(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String respXml = wxService.callbackEvent(request,response);
        out.print(respXml);
        out.close();
        out = null;
    }

    /**
     * 上传永久素材
     *
     * @return
     * @throws WxErrorException
     */
    @RequestMapping(value = "uploadPermanent",method = RequestMethod.POST)
    public String uploadPermanent() throws WxErrorException {
        File file = new File("/Users/sim/Desktop/111.jpg");
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName("logo");
        WxMpMaterialUploadResult wxMpMaterialUploadResult = wxMpService.getMaterialService().materialFileUpload(WxConsts.MediaFileType.IMAGE, wxMpMaterial);
        redisUtil.setCacheObject("MediaId",wxMpMaterialUploadResult.getMediaId());
        return "上传永久素材成功：mediaId:" + wxMpMaterialUploadResult.getMediaId();
    }

    /**
     * 上传临时素材
     *
     * @return
     * @throws WxErrorException
     */
    @RequestMapping(value = "uploadTemp",method = RequestMethod.POST)
    public String uploadTemp() throws WxErrorException {
        File file = new File("/Users/sim/Desktop/erweima.0df50348.jpg");
        WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
        return "上传临时素材成功：mediaId:" + wxMediaUploadResult.getMediaId();
    }


}
