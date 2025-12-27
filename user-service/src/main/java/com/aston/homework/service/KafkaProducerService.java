package com.aston.homework.service;

import com.aston.homework.dto.EventName;
import com.aston.homework.dto.UserDtoOut;

public interface KafkaProducerService {
    void sendEvent(EventName eventName, UserDtoOut userDtoOut);
}
