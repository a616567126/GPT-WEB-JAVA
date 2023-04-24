package com.chat.java.utils;

import cn.hutool.core.util.RandomUtil;
import com.chat.java.exception.CustomException;
import com.chat.java.model.EmailConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.List;
import java.util.Properties;
import java.util.Random;


@Component
@Log4j2
public class EmailServiceUtil {

    @Resource
    RedisUtil redisUtil;

    public String emailForm = "";

    public void sendEmail(String email)  {
        Session session = createSession();
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(emailForm));
            msg.setRecipients(Message.RecipientType.TO, email);
            msg.setSubject("注册验证码","utf-8");
            BodyPart textPart = new MimeBodyPart();
            String code = RandomUtil.randomNumbers(6);
            textPart.setContent("<h1>您好，您的验证码为："+code+"</h1>","text/html;charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            msg.setContent(multipart);
            Transport.send(msg);
            redisUtil.setCacheObject("EMAIL:"+email,code);
        } catch (MessagingException e) {
            log.error("当前邮件异常：{},重新发送",emailForm);
            List<EmailConfig> emailList = RedisUtil.getCacheObject("emailList");
            emailList.removeIf((EmailConfig config)-> config.getUsername().equals(emailForm));
            redisUtil.setCacheObject("emailList",emailList);
            sendEmail(email);
        }
    }


    public Session createSession(){
        List<EmailConfig> emailList = RedisUtil.getCacheObject("emailList");
        if(null == emailList || emailList.size() < 1){
            throw new CustomException("暂无可用的邮件服务");
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
