package com.aston.homework.service.impl;

import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.service.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {
    private EmailService emailService;
    private ByteArrayOutputStream outputStream;
    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(javaMailSender);
        emailService.setFromEmail("bnbn2121@mail.ru");
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }

    @Test
    void shouldSendEmailSuccess() {
        // Given
        String email = "test@mail.com";
        String subject = "testSubject";
        String message = "testMessage";
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendEmail(email, subject, message);

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("message successfully sent"));
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSendEmailFailed() {
        // Given
        String email = "test@mail.com";
        String subject = "testSubject";
        String message = "testMessage";
        doThrow(MailSendException.class).when(javaMailSender).send(any(SimpleMailMessage.class));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendEmail(email, subject, message));

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("error sending message"));
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUserEventSuccess() {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, "test@mail.ru");
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendUserEventByEmail(eventDto);

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("message successfully sent"));
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUserEventWithNullEvent() {
        // Given
        EventDto eventDto = null;

        // When & Then
        assertThrows(IllegalStateException.class, () -> emailService.sendUserEventByEmail(eventDto));
        String output = outputStream.toString();
        assertTrue(output.contains("error eventDto state"));
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUserEventWithNullEmail() {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, null);

        // When & Then
        assertThrows(IllegalStateException.class, () -> emailService.sendUserEventByEmail(eventDto));
        String output = outputStream.toString();
        assertTrue(output.contains("error eventDto state"));
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUserEventWithNullTypeEvent() {
        // Given
        EventDto eventDto = new EventDto(null, "test@mail.ru");

        // When & Then
        assertThrows(IllegalStateException.class, () -> emailService.sendUserEventByEmail(eventDto));
        String output = outputStream.toString();
        assertTrue(output.contains("error eventDto state"));
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUserEventWithErrorTypeEvent() {
        // Given
        EventDto eventDto = new EventDto(EventName.FOR_TEST, "test@mail.ru");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> emailService.sendUserEventByEmail(eventDto));
        String output = outputStream.toString();
        assertTrue(output.contains("Event name not found"));
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }
}
