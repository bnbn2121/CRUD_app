package com.aston.homework.service;

import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 130;


    public void validateData(String name, String email, int age) throws UserServiceException {
        String message = null;
        if (name == null || name.isBlank()) {
            message = "user name cannot be empty";
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        }

        if (email == null || email.isBlank()) {
            message = "email cannot be empty";
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        } else if (!email.contains("@")) {
            message = "email must contains \"@\"";
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        }

        if (age < MIN_AGE || age > MAX_AGE) {
            message = "age must be between %d and %d".formatted(MIN_AGE, MAX_AGE);
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        }
    }

    public void validateData(UserDtoIn user) throws UserServiceException {
        String message = null;
        if (user.getName() == null || user.getName().isBlank()) {
            message = "user name cannot be empty";
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            message = "email cannot be empty";
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        } else if (!user.getEmail().contains("@")) {
            message = "email must contains \"@\"";
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        }

        if (user.getAge() < MIN_AGE || user.getAge() > MAX_AGE) {
            message = "age must be between %d and %d".formatted(MIN_AGE, MAX_AGE);
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        }
    }

    public void validateId(int id) throws UserServiceException {
        if (id <= 0) {
            String message = "id cannot be <0";
            logger.info("validation unsuccess: {}", message);
            throw new UserServiceException(message);
        }
    }

    public void normalizeEmail(UserDtoIn user) {
        if (user != null) {
            user.setEmail(user.getEmail().toLowerCase());
        }
    }

    public String normalizeEmail(String email) {
        if (email != null) {
            return email.toLowerCase();
        }
        return null;
    }
}
