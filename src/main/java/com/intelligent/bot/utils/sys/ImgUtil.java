package com.intelligent.bot.utils.sys;

import cn.hutool.core.date.DateUtil;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;


@Log4j2
public class ImgUtil {
    /**
     * 上传图片(multipartFile)
     * @return
     * @throws IOException
     */
    public static String uploadMultipartFile(MultipartFile multipartFile,String oldFileName) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }
        return uploadBufferedImage(new BufferedInputStream(multipartFile.getInputStream()),oldFileName);
    }
    /**
     * 上传图片(BufferedInputStream)
     * @param buff
     * @return
     * @throws IOException
     */
    public static String uploadBufferedImage(BufferedInputStream buff,String oldFileName) throws IOException {
        String[] fileStr = createFileName();
//        String fileName = "/"+fileStr[1]+"/"+oldFileName + ".jpg";
        String fileName = "/"+fileStr[1] +"/"+ System.currentTimeMillis() + ".jpg";
        String savePath = fileStr[0]+ fileName;
        OutputStream os = Files.newOutputStream(Paths.get(savePath));
        int len;
        byte[] arr = new byte[1024];
        while ((len = buff.read(arr)) != -1) {
            os.write(arr, 0, len);
            os.flush();
        }
        os.close();
        //是否开启压缩
        PicUtils.commpressPicForScale(fileName,fileName,0.8f);
        return fileName;
    }

    public static String[] createFileName(){
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String newFileName =  DateUtil.format(new Date(), "yyyyMMdd");
        String uploadPath = sysConfig.getImgUploadUrl();
//        log.info("上传路径="  + uploadPath);
        File destFile = new File(uploadPath + newFileName);
        if (!destFile.exists()) {
            //不存在就创建
            destFile.mkdir();
        }
        return new String[]{uploadPath,newFileName};
    }
    public static void deleteFile(String filePath) throws IOException {
        log.info("删除文件路径：{}",filePath);
        File file = new File(filePath);
        file.delete();
    }
}
