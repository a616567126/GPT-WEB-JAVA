package com.intelligent.bot.utils.sys;

import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;


@Log4j2
public class PicUtils {
    /**
     * 根据指定大小和指定精度压缩图片
     *
     * @param srcPath     源图片地址
     * @param desPath     目标图片地址
     * @return
     */
    public static String commpressPicForScale(String srcPath, String desPath,float scale) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        srcPath = sysConfig.getImgUploadUrl() + srcPath;
        desPath = sysConfig.getImgUploadUrl() + desPath;
        if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(srcPath)) {
            return null;
        }
//        if (!new File(srcPath).exists()) {
//            return null;
//        }
//        try {
//            File srcFile = new File(srcPath);
//            long srcFileSize = srcFile.length();
////            log.info("源图片：" + srcPath + "，大小：" + srcFileSize / 1024
////                    + "kb");
//            // 1、先转换成jpg
//            if(!desPath.contains("jpg")){
//                Thumbnails.of(srcPath).scale(1f).toFile(desPath);
//            }
//            // 递归压缩，直到目标文件大小小于desFileSize
//            commpressPicCycle(desPath, 500L, 0.9);
//
//            File desFile = new File(desPath);
////            log.info("目标图片：" + desPath + "，大小" + desFile.length()
////                    / 1024 + "kb");
////            log.info("图片压缩完成！");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        cn.hutool.core.img.ImgUtil.scale(new File(srcPath), new File(desPath), scale);
        return desPath;
    }

    private static void commpressPicCycle(String desPath, long desFileSize,
                                          double accuracy) throws IOException {
        File srcFileJPG = new File(desPath);
        long srcFileSizeJPG = srcFileJPG.length();
        // 2、判断大小，如果小于500kb，不压缩；如果大于等于500kb，压缩
        if (srcFileSizeJPG <= desFileSize * 1024) {
            return;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(srcFileJPG);
        int srcWdith = bim.getWidth();
        int srcHeigth = bim.getHeight();
        int desWidth = new BigDecimal(srcWdith).multiply(
                new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(srcHeigth).multiply(
                new BigDecimal(accuracy)).intValue();

        Thumbnails.of(desPath).size(desWidth, desHeight)
                .outputQuality(accuracy).toFile(desPath);
        commpressPicCycle(desPath, desFileSize, accuracy);
    }


}
