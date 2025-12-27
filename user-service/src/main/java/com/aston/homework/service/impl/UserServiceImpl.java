package com.aston.homework.service.impl;

import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.entity.User;
import com.aston.homework.repository.UserRepository;
import com.aston.homework.service.UserService;
import com.aston.homework.service.UserServiceException;
import com.aston.homework.service.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserRepository userRepository;
    private UserValidator userValidator;

    public UserServiceImpl(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Transactional(readOnly = true)
    public UserDtoOut getUserById(int id) throws UserServiceException {
        logger.info("Finding user by id: {}", id);
        userValidator.validateId(id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.info("user not found");
            throw new UserServiceException("user not found");
        }
        return convertToDTO(optionalUser.get());
    }

    @Transactional
    public UserDtoOut addUser(UserDtoIn userDtoIn) throws UserServiceException {
        logger.info("Saving new user");
        userValidator.validateData(userDtoIn);
        userValidator.normalizeEmail(userDtoIn);
        if (userRepository.existsByEmail(userDtoIn.getEmail())) {
            logger.info("saving user is unavailable, such email exists");
            throw new UserServiceException("saving is unavailable, such email exists");
        }
        User user = new User(userDtoIn.getName(), userDtoIn.getEmail(), userDtoIn.getAge());
        return convertToDTO(userRepository.save(user));
    }

    @Transactional
    public UserDtoOut updateUserById(int id, UserDtoIn userDtoIn) throws UserServiceException {
        logger.info("Updating user with id={}", id);
        userValidator.validateId(id);
        userValidator.validateData(userDtoIn);
        userValidator.normalizeEmail(userDtoIn);

        String newName = userDtoIn.getName();
        String newEmail = userDtoIn.getEmail();
        int newAge = userDtoIn.getAge();

        User userForUpdate = userRepository.findById(id).orElseThrow(() ->
                new UserServiceException("user with id=%d not found".formatted(id)));
        if (!newEmail.equals(userForUpdate.getEmail()) && userRepository.existsByEmail(newEmail)) {
            logger.info("this email already used");
            throw new UserServiceException("this email already used");
        }
        userForUpdate.setName(newName);
        userForUpdate.setEmail(newEmail);
        userForUpdate.setAge(newAge);
        return convertToDTO(userRepository.save(userForUpdate));
    }

    @Transactional
    public boolean deleteUser(int id) throws UserServiceException {
        logger.info("Deleting user with id={}", id);
        userValidator.validateId(id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UserDtoOut convertToDTO(User user) {
        UserDtoOut userDtoOut = null;
        if (user != null) {
            userDtoOut = new UserDtoOut(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getAge(),
                    user.getCreatedAt()
            );
        }
        return userDtoOut;
    }
}
