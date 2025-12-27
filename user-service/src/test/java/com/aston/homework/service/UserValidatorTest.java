package com.aston.homework.service;

import com.aston.homework.dto.UserDtoIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    void shouldValidateDataSuccesfull() throws Exception{
        // Given
        String name = "test";
        String email = "test@test.com";
        int age = 10;

        // When & Then
        assertDoesNotThrow(() -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateDataWithNameIsNull() throws Exception{
        // Given
        String name = null;
        String email = "test@test.com";
        int age = 10;

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateDataWithNameIsEmpty() throws Exception{
        // Given
        String name = "";
        String email = "test@test.com";
        int age = 10;

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateDataWithNameIsBlank() throws Exception{
        // Given
        String name = "   ";
        String email = "test@test.com";
        int age = 10;

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateDataWithEmailIsNull() throws Exception{
        // Given
        String name = "test";
        String email = null;
        int age = 10;

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateDataWithEmailIsEmpty() throws Exception{
        // Given
        String name = "test";
        String email = "";
        int age = 10;

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateDataWithEmailIsBlank() throws Exception{
        // Given
        String name = "test";
        String email = "    ";
        int age = 10;

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateDataWithEmailNotContainsSpecialSymbol() throws Exception{
        // Given
        String name = "test";
        String email = "test";
        int age = 10;

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 131, 999})
    void shouldValidateDataWithIncorrectAge(int age) throws Exception{
        // Given
        String name = "test";
        String email = "test@test.com";

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(name, email, age));
    }

    @Test
    void shouldValidateUserDataSuccesfull() throws Exception{
        // Given
        String name = "test";
        String email = "test@test.com";
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertDoesNotThrow(() -> userValidator.validateData(user));
    }

    @Test
    void shouldValidateUserDataWithNameIsNull() throws Exception{
        // Given
        String name = null;
        String email = "test@test.com";
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @Test
    void shouldValidateUserDataWithNameIsEmpty() throws Exception{
        // Given
        String name = "";
        String email = "test@test.com";
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @Test
    void shouldValidateUserDataWithNameIsBlank() throws Exception{
        // Given
        String name = "   ";
        String email = "test@test.com";
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @Test
    void shouldValidateUserDataWithEmailIsNull() throws Exception{
        // Given
        String name = "test";
        String email = null;
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @Test
    void shouldValidateUserDataWithEmailIsEmpty() throws Exception{
        // Given
        String name = "test";
        String email = "";
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @Test
    void shouldValidateUserDataWithEmailIsBlank() throws Exception{
        // Given
        String name = "test";
        String email = "    ";
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @Test
    void shouldValidateUserDataWithEmailNotContainsSpecialSymbol() throws Exception{
        // Given
        String name = "test";
        String email = "test";
        int age = 10;
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 131, 999})
    void shouldValidateUserDataWithIncorrectAge(int age) throws Exception{
        // Given
        String name = "test";
        String email = "test@test.com";
        UserDtoIn user = new UserDtoIn(name, email, age);

        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateData(user));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 100, 1000})
    void shouldValidateIdCorrect(int id) throws Exception{
        // When & Then
        assertDoesNotThrow(() -> userValidator.validateId(id));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0})
    void shouldValidateIdIncorrect(int id) throws Exception{
        // When & Then
        assertThrows(UserServiceException.class, () -> userValidator.validateId(id));
    }

    @Test
    void shouldNormalizeEmailWithString(){
        // Given
        String email = "TeSt@test.COM";

        // When
        String normilizedEmail = userValidator.normalizeEmail(email);

        // Then
        assertEquals(email.toLowerCase(), normilizedEmail);
    }

    @Test
    void shouldNormalizeEmailWithUser(){
        // Given
        String email = "TeSt@test.COM";
        UserDtoIn user = new UserDtoIn("name", email, 10);

        // When
        userValidator.normalizeEmail(user);

        // Then
        assertEquals(email.toLowerCase(), user.getEmail());
    }
}
