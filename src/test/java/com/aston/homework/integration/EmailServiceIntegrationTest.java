package com.aston.homework.integration;

import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.service.impl.EmailServiceImpl;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
public class EmailServiceIntegrationTest {

    private GreenMail greenMail;

    @Autowired
    private EmailServiceImpl emailService;

    @TestConfiguration
    static class TestMailConfig {
        @Bean
        public JavaMailSender javaMailSender() {
            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost("localhost");
            javaMailSender.setPort(3025);
            javaMailSender.getJavaMailProperties().put("mail.smtp.auth", "false");
            javaMailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "false");
            return javaMailSender;
        }
    }

    @BeforeEach
    void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @AfterEach
    void stopMailServer() {
        greenMail.stop();
    }

    @Test
    void shouldSendRealEmail() throws Exception {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, "test@recipient.com");

        // When
        emailService.sendUserEventByEmail(eventDto);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertTrue(receivedMessages.length == 1);
        Message message = receivedMessages[0];
        assertEquals(message.getSubject(), "Notification message");
        assertEquals(message.getContent().toString(), "Здравствуйте! Ваш аккаунт был успешно создан");
        assertEquals(message.getAllRecipients()[0].toString(), "test@recipient.com");
    }
}