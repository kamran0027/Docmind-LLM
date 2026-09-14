package com.kamran.Docmind.services;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    EmailService(JavaMailSender javaMailSender){
        this.javaMailSender=javaMailSender;
    }

    public boolean sendResetPasswordLink(String email,String token){
        String link="http://localhost:8080/auth/reset-password?token="+token;
        try{
            SimpleMailMessage msg=new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("forgot password link");
            msg.setText("click on link to reset the password :\n"+link);
            javaMailSender.send(msg);
            return true;
        }catch (MailException e){
            e.printStackTrace();
            return false;
        }
        
    }

}
