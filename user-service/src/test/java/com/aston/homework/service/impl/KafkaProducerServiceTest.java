package com.aston.homework.service.impl;

import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.dto.UserDtoOut;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {
    private ByteArrayOutputStream outputStream;

    @Mock
    private KafkaTemplate<String, EventDto> kafkaTemplate;

    @InjectMocks
    private KafkaProducerServiceImpl kafkaProducerService;

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
    void shouldSendEventSuccess() {
        //Given
        EventName eventName = EventName.CREATE;
        UserDtoOut userDtoOut = new UserDtoOut(1, "testName", "test@mail.com", 25, LocalDateTime.now());
        CompletableFuture<SendResult<String, EventDto>> future = CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(eq("user_events_topic"), any(EventDto.class))).thenReturn(future);

        // When
        kafkaProducerService.sendEvent(eventName, userDtoOut);

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("event successfully sent to kafka"));
        verify(kafkaTemplate).send(eq("user_events_topic"), any(EventDto.class));
    }

    @Test
    void shouldSendEventFailed() {
        //Given
        EventName eventName = EventName.CREATE;
        UserDtoOut userDtoOut = new UserDtoOut(1, "testName", "test@mail.com", 25, LocalDateTime.now());
        CompletableFuture<SendResult<String, EventDto>> future = CompletableFuture.failedFuture(new RuntimeException());

        when(kafkaTemplate.send(eq("user_events_topic"), any(EventDto.class))).thenReturn(future);

        // When
        kafkaProducerService.sendEvent(eventName, userDtoOut);

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("event not delivered to kafka"));
        verify(kafkaTemplate).send(eq("user_events_topic"), any(EventDto.class));

    }
}
