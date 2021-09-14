package com.himanshudabas.springboot.travelticketing.service.email;

import com.himanshudabas.springboot.travelticketing.constant.EmailConstant;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void send(String to, String password) throws SendEmailFailException {
        log.info("inside send() of emailService, sending email to: {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EmailConstant.FROM_EMAIL);
        message.setTo(to);
        message.setSubject(EmailConstant.EMAIL_SUBJECT);
        message.setText(String.format(EmailConstant.EMAIL_BODY_TEMPLATE, to, password));
        try {
            emailSender.send(message);
        } catch (Exception ex) {
            throw new SendEmailFailException(EmailConstant.EMAIL_EXCEPTION_MESSAGE);
        }
        log.info("finished send()");
    }
}
