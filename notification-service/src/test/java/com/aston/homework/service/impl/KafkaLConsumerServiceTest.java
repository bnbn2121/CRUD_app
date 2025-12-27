package com.aston.homework.service.impl;

import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.service.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaLConsumerServiceTest {
    private ByteArrayOutputStream outputStream;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }

    @Test
    void shouldHandleEventSuccess() {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, "test@mail.com");
        doNothing().when(emailService).sendUserEventByEmail(eventDto);

        // When
        kafkaConsumerService.handleEvent(eventDto);

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("handled event by consumer"));
        verify(emailService).sendUserEventByEmail(eventDto);
    }
}
