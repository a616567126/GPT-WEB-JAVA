package com.intelligent.bot.api.sys.admin;

import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.sys.admin.SysConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SysConfigQueryRes;
import com.intelligent.bot.model.res.sys.admin.SysConfigUploadImgRes;
import com.intelligent.bot.service.sys.ISysConfigService;
import com.intelligent.bot.utils.sys.ImgUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;


@RestController
@RequestMapping("/sys/config")

public class SysConfigController {


    @Resource
    ISysConfigService payConfigService;


    @RequestMapping(value = "/queryPage", name = "查询payConfig",method = RequestMethod.POST)
    public B<SysConfigQueryRes> querySysConfig() {
        return payConfigService.queryPage();
    }


    @RequestMapping(value = "/update",name = "更新payConfig", method = RequestMethod.POST)
    public B<Void> update(@Validated @RequestBody SysConfigUpdateReq req) {
        return payConfigService.update(req);
    }

    @RequestMapping(value = "/upload/img", name = "上传图片")
    public B<SysConfigUploadImgRes> uploadImg(MultipartFile file) throws IOException {
        String oldFileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
        String fileName = ImgUtil.uploadMultipartFile(file, oldFileName);
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        SysConfigUploadImgRes res = SysConfigUploadImgRes.builder()
                .imageUrl(cacheObject.getImgReturnUrl() + fileName)
                .filePatch(fileName).build();
        return B.okBuild(res);
    }
}
