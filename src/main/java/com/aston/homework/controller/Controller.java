package com.aston.homework.controller;

import com.aston.homework.dto.EventDto;
import com.aston.homework.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/send")
public class Controller {
    private final EmailService emailService;

    public Controller(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody EventDto eventDto) {
        try {
            emailService.sendUserEventByEmail(eventDto);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
