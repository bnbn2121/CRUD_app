package com.aston.homework.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User("test", "test@test.com", 25);
        user2 = new User("test", "test@test.com", 25);

        setIdViaReflection(user1, 1);
        setIdViaReflection(user2, 1);
        setCreatedAtViaReflection(user1, LocalDateTime.of(2025, 1, 1, 1, 1));
        setCreatedAtViaReflection(user2, LocalDateTime.of(2025, 1, 1, 1, 1));
    }

    private void setIdViaReflection(User user, int id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("reflection error", e);
        }
    }

    private void setCreatedAtViaReflection(User user, LocalDateTime createdAt) {
        try {
            Field createdAtField = User.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(user, createdAt);
        } catch (Exception e) {
            throw new RuntimeException("reflection error", e);
        }
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreSame() {
        // When & Then
        assertEquals(user1, user2);
        assertEquals(user2, user1);
    }

    @Test
    void shouldNotBeEqualWhenSecondObjectIsNull() {
        // When & Then
        assertNotEquals(user1, null);
    }

    @Test
    void shouldBeEqualWhensSecondObjectIsItself() {
        // When & Then
        assertEquals(user1, user1);
    }

    @Test
    void shouldNotBeEqualWhensSecondObjectIsAnotherClass() {
        String string = "string";
        // When & Then
        assertNotEquals(user1, string);
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        //Given
        setIdViaReflection(user2, 2);

        // When & Then
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentAge() {
        //Given
        user2.setAge(33);

        // When & Then
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentName() {
        //Given
        user2.setName("test2");

        // When & Then
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentEmail() {
        //Given
        user2.setEmail("aaa@aaa.aaa");

        // When & Then
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentCreatedAt() {
        //Given
        setCreatedAtViaReflection(user2, LocalDateTime.now());

        // When & Then
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldBeEqualByHashcode() {
        // When & Then
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
