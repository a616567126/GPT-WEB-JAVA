package com.intelligent.bot.model.res.sys.admin;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Builder
public class SysConfigUploadImgRes implements Serializable {

    private String filePatch;

    private String imageUrl;

}
