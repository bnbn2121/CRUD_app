package com.aston.homework.service;

import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;

public interface UserService {
    UserDtoOut getUserById(int id) throws UserServiceException;
    UserDtoOut addUser(UserDtoIn userDtoIn) throws UserServiceException;
    UserDtoOut updateUserById(int id, UserDtoIn userDtoIn) throws UserServiceException;
    boolean deleteUser(int id) throws UserServiceException;
}
