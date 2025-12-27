package com.aston.homework.service.impl;

import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.entity.User;
import com.aston.homework.repository.UserRepository;
import com.aston.homework.service.UserServiceException;
import com.aston.homework.service.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator validator;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, validator);
    }

    @Test
    void shouldGetUserByExistsId() throws Exception {
        // Given
        int userId = 1;
        User user = new User("testUser", "test@mail.ru", 25);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDtoOut userObtained = userService.getUserById(userId);

        // Then
        assertNotNull(userObtained);
        assertEquals("testUser", userObtained.getName());
        assertEquals("test@mail.ru", userObtained.getEmail());
        assertEquals(25, userObtained.getAge());
        verify(userRepository).findById(userId);
    }


    @Test
    void shouldGetUserByNotExistsId() throws Exception {
        // Given
        int userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.getUserById(userId));
        assertEquals("user not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldAddUserWithUniqueEmail() throws Exception {
        // Given
        UserDtoIn userDtoIn = new UserDtoIn("testUser", "test@mail.ru", 25);
        User user = new User(userDtoIn.getName(), userDtoIn.getEmail(), userDtoIn.getAge());

        when(userRepository.existsByEmail(userDtoIn.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // When
        UserDtoOut userAfterAdd = userService.addUser(userDtoIn);

        // Then
        assertNotNull(userAfterAdd);
        assertEquals("testUser", userAfterAdd.getName());
        assertEquals("test@mail.ru", userAfterAdd.getEmail());
        assertEquals(25, userAfterAdd.getAge());
        verify(userRepository).existsByEmail(userDtoIn.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void shouldAddUserWithExistsEmail() throws Exception {
        // Given
        UserDtoIn userDtoIn = new UserDtoIn("testUser", "test@mail.ru", 25);

        when(userRepository.existsByEmail(userDtoIn.getEmail())).thenReturn(true);

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.addUser(userDtoIn));
        assertEquals("saving is unavailable, such email exists", exception.getMessage());
        verify(userRepository).existsByEmail(userDtoIn.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateExistsUserWithUniqueEmail() throws Exception {
        // Given
        int id = 1;
        UserDtoIn userDtoIn = new UserDtoIn("new name", "new@email.com", 25);

        User foundedUserInDB = new User("old name", "old@email.com", 10);
        when(userRepository.findById(id)).thenReturn(Optional.of(foundedUserInDB));
        when(userRepository.existsByEmail(userDtoIn.getEmail())).thenReturn(false);
        when(userRepository.save(foundedUserInDB)).thenReturn(foundedUserInDB);

        // When
        UserDtoOut userDtoOut = userService.updateUserById(id, userDtoIn);

        // Then
        assertNotNull(userDtoOut);
        assertEquals(userDtoIn.getEmail(), userDtoOut.getEmail());
        assertEquals(userDtoIn.getName(), userDtoOut.getName());
        assertEquals(userDtoIn.getAge(), userDtoOut.getAge());
        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail(userDtoIn.getEmail());
        verify(userRepository).save(foundedUserInDB);
    }

    @Test
    void shouldUpdateUserWithExistsEmail() throws Exception {
        // Given
        int id = 1;
        UserDtoIn userDtoIn = new UserDtoIn("new name", "new@email.com", 25);

        User foundedUserInDB = new User("old name", "old@email.com", 10);
        when(userRepository.findById(id)).thenReturn(Optional.of(foundedUserInDB));
        when(userRepository.existsByEmail(userDtoIn.getEmail())).thenReturn(true);

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.updateUserById(id, userDtoIn));
        assertEquals("this email already used", exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail(userDtoIn.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUserWithNotExistsUser() throws Exception {
        // Given
        int id = 1;
        UserDtoIn userDtoIn = new UserDtoIn("new name", "new@email.com", 25);

        User foundedUserInDB = new User("old name", "old@email.com", 10);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.updateUserById(id, userDtoIn));
        assertEquals("user with id=%d not found".formatted(id), exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUserWithNotExistsId() throws Exception {
        // Given
        int id = 999;

        when(userRepository.existsById(id)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(id);

        //Then
        assertFalse(result);
        verify(userRepository).existsById(id);
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldDeleteUserWithExistsId() throws Exception {
        // Given
        int id = 1;

        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository).deleteById(id);

        // When
        boolean result = userService.deleteUser(id);

        //Then
        assertTrue(result);
        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }

}
