package com.aston.homework.service.impl;

import com.aston.homework.dto.EventDto;
import com.aston.homework.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String DEFAULT_SUBJECT = "Notification message";
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String email, String subject, String message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(fromEmail);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);
            logger.info("message successfully sent");
        } catch (MailException e) {
            logger.error("error sending message");
            throw new RuntimeException("Email service error", e);
        }
    }

    @Override
    public void sendUserEventByEmail(EventDto eventDto) {
        logger.info("sending message");
        if (eventDto == null || eventDto.getEvent() == null || eventDto.getEmail() == null) {
            logger.debug("error eventDto state");
            throw new IllegalStateException("error eventDto state"); // сделать когда-нибудь обратную отправку в сервис об ошибке
        }
        String message = switch (eventDto.getEvent()) {
            case CREATE -> "Здравствуйте! Ваш аккаунт был успешно создан";
            case DELETE -> "Здравствуйте! Ваш аккаунт был удалён";
            default -> null;
        };
        if (message == null) {
            logger.debug("Event name not found");
            throw new IllegalArgumentException("Event name not found");
        }
        sendEmail(eventDto.getEmail(), DEFAULT_SUBJECT, message);
    }
}
