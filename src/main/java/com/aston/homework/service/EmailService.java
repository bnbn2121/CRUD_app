package com.aston.homework.service;

import com.aston.homework.dto.EventDto;

public interface EmailService {
    void setFromEmail(String fromEmail);
    void sendEmail(String email, String subject, String message);
    void sendUserEventByEmail(EventDto eventDto);
}
