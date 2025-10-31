package com.aston.homework.service.impl;

import com.aston.homework.dto.EventDto;
import com.aston.homework.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user_events_topic", groupId = "email_notification")
    public void handleEvent(EventDto eventDto) {
        System.out.println("получено из кафки");
        logger.info("receiving event from kafka");
        emailService.sendUserEventByEmail(eventDto);
    }
}
