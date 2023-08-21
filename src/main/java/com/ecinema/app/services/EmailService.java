package com.ecinema.app.services;

import com.ecinema.app.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final String BUSINESS = "ECinema";

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendFromBusinessEmail(String to, String email, String subject)
            throws EmailException {
        send(BUSINESS, to, email, subject);
    }

    public void send(String from, String to, String email, String subject)
            throws EmailException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setFrom(BUSINESS);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }
    }

    public String getBusinessEmail() {
        return BUSINESS;
    }

}
