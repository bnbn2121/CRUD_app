package com.aston.homework.controller;

import com.aston.homework.controller.Controller;
import com.aston.homework.dto.EventDto;
import com.aston.homework.dto.EventName;
import com.aston.homework.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
public class ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @Test
    void shouldSendMessageSuccess() throws Exception {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, "test@mail.com");

        doNothing().when(emailService).sendUserEventByEmail(any(EventDto.class));

        // When & Then
        mockMvc.perform(post("/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
        verify(emailService).sendUserEventByEmail(any(EventDto.class));
    }

    @Test
    void shouldSendMessageWithException() throws Exception {
        // Given
        EventDto eventDto = new EventDto(EventName.CREATE, "test@mail.com");

        doThrow(RuntimeException.class).when(emailService).sendUserEventByEmail(any(EventDto.class));

        // When & Then
        mockMvc.perform(post("/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
        verify(emailService).sendUserEventByEmail(any(EventDto.class));
    }
}
