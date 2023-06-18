package com.intelligent.bot.utils.sys;


import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.utils.gpt.Proxys;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class FileUtil {

    public static String base64ToImage(String base64,String fileName) throws IOException {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        // JDK8以上
        Base64.Decoder decoder = Base64.getDecoder();
        // Base64解码
        byte[] bytes = decoder.decode(base64);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        String imgUploadUrl =  cacheObject.getImgUploadUrl();
        String dayFilePatch = DateUtil.format(new Date(), "yyyyMMdd");
        File destFile = new File(imgUploadUrl+dayFilePatch);
        if (!destFile.exists()) {
            //不存在就创建
            destFile.mkdir();
        }
        if(StringUtils.isEmpty(fileName)){
            fileName = String.valueOf(System.currentTimeMillis());
        }
        String newFileName ="/"+fileName+".jpg";
        // 生成jpeg图片
        OutputStream out = Files.newOutputStream(Paths.get(imgUploadUrl+dayFilePatch+newFileName));
        out.write(bytes);
        out.flush();
        out.close();
        String savePath = "/"+dayFilePatch+newFileName;
        PicUtils.commpressPicForScale(savePath,savePath);
        return savePath;
    }

    public static String base64ToImage(String base64) throws IOException {
        return base64ToImage(base64,null);
    }

    /**
     * 图片URL转Base64编码
     *
     * @param imgUrl 图片URL
     * @return Base64编码
     */
    public static String imageUrlToBase64(String imgUrl) {
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        try {
            if (!StringUtils.isEmpty(imgUrl)) {
                SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
                Proxy proxy = null ;
                if(null != sysConfig.getIsOpenProxy() && sysConfig.getIsOpenProxy() == 1){
                    proxy = Proxys.http(sysConfig.getProxyIp(), sysConfig.getProxyPort());
                }
                HttpResponse res =
                        HttpRequest
                                .get(imgUrl)
                                .setProxy(proxy)
                                .execute();
                // 获取输入流
                is = res.bodyStream();
                outStream = new ByteArrayOutputStream();
                //创建一个Buffer字符串
                byte[] buffer = new byte[1024];
                //每次读取的字符串长度，如果为-1，代表全部读取完毕
                int len = 0;
                //使用输入流从buffer里把数据读取出来
                while ((len = is.read(buffer)) != -1) {
                    //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                    outStream.write(buffer, 0, len);
                }
                // 对字节数组Base64编码
                return encode(outStream.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 图片转字符串
     *
     * @param image 图片Buffer
     * @return Base64编码
     */
    public static String encode(byte[] image) {
        return replaceEnter(Base64.getEncoder().encodeToString(image));
    }

    /**
     * 字符替换
     *
     * @param str 字符串
     * @return 替换后的字符串
     */
    public static String replaceEnter(String str) {
        String reg = "[\n-\r]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    public static List<String> readFiles(String folderPath) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) { // 确定文件夹存在且是文件夹
            File[] files = folder.listFiles(); // 列出文件夹下全部文件和文件夹
            assert files != null;
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                } else {
                    // 如果是文件夹，递归读取其中的文件
                    readFiles(file.getAbsolutePath());
                }
            }
        } else {
            log.info("文件夹不存在或不是一个文件夹！");
        }
        return fileNames;
    }

}
