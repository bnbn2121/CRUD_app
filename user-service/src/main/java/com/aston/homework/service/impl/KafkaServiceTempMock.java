package com.aston.homework.service.impl;

import com.aston.homework.dto.EventName;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.service.KafkaProducerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false")
public class KafkaServiceTempMock implements KafkaProducerService {

    @Override
    public void sendEvent(EventName eventName, UserDtoOut userDtoOut) {
        return;
    }
}
