package com.intelligent.bot.service.sys;


import cn.hutool.core.util.RandomUtil;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.EmailConfig;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Component
@Log4j2
public class SendMessageService {

    public String emailForm = "";


    @Resource
    RedisUtil redisUtil;

    public void sendEmail(String email){
        Session session = createSession();
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(emailForm,CommonConst.EMAIL_TITLE));
            msg.setRecipients(Message.RecipientType.TO, email);
            msg.setSubject("注册验证码","utf-8");
            BodyPart textPart = new MimeBodyPart();
            String code = RandomUtil.randomNumbers(6);
            textPart.setContent("<h1>您好，您的验证码为："+code+"</h1>","text/html;charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            msg.setContent(multipart);
            Transport.send(msg);
            redisUtil.setCacheObject(CommonConst.SEND_EMAIL_CODE + email,code);
        } catch (MessagingException e) {
            log.error("当前邮件异常：{},重新发送",emailForm);
            List<EmailConfig> emailList = RedisUtil.getCacheObject(CommonConst.EMAIL_LIST);
            emailList.removeIf((EmailConfig config)-> config.getUsername().equals(emailForm));
            redisUtil.setCacheObject(CommonConst.EMAIL_LIST,emailList);
            sendEmail(email);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Session createSession(){
        List<EmailConfig> emailList = RedisUtil.getCacheObject(CommonConst.EMAIL_LIST);
        if(null == emailList || emailList.size() < 1){
            throw new E("暂无可用的邮件服务");
        }
        EmailConfig emailConfig = getListElementRandom(emailList);
        Properties properties = new Properties();
        properties.put("mail.smtp.host",emailConfig.getHost());
        properties.put("mail.smtp.port",emailConfig.getPort());
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        if(emailConfig.getHost().contains("qq")){
            properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.port",emailConfig.getPort());
            properties.put("mail.smtp.starttls.required","true");
        }
        emailForm = emailConfig.getUsername();
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getUsername(),emailConfig.getPassword());
            }
        });
        session.setDebug(true);
        return session;
    }

    public static EmailConfig getListElementRandom(List<EmailConfig> list){
        return list.get(new Random().nextInt(list.size()));
    }
}
