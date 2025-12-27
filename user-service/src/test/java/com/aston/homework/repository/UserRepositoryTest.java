package com.aston.homework.repository;
import com.aston.homework.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
public class UserRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        // Given
        User user = new User("testUser", "test@mail.ru", 25);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotEquals(0, savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getAge(), savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    private User presaveUserInDB() {
        User presavedUser = new User("testUser", "test@mail.ru", 25);
        return userRepository.save(presavedUser);
    }

    @Test
    void shouldFindUserById() {
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUser = userRepository.findById(presavedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(presavedUser.getId(), foundUser.get().getId());
        assertEquals(presavedUser.getName(), foundUser.get().getName());
        assertEquals(presavedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(presavedUser.getAge(), foundUser.get().getAge());
        assertEquals(presavedUser.getCreatedAt(), foundUser.get().getCreatedAt());
    }

    @Test
    void shouldFindUserByNotExistsId() {
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUserNotExistsId = userRepository.findById(presavedUser.getId()+1);

        // Then
        assertTrue(foundUserNotExistsId.isEmpty());
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUser = userRepository.findByEmail(presavedUser.getEmail());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(presavedUser.getId(), foundUser.get().getId());
        assertEquals(presavedUser.getName(), foundUser.get().getName());
        assertEquals(presavedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(presavedUser.getAge(), foundUser.get().getAge());
        assertEquals(presavedUser.getCreatedAt(), foundUser.get().getCreatedAt());
    }

    @Test
    void shouldFindUserByNotExistsEmail() {
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUserNotExistsEmail = userRepository.findByEmail("notExistEmail@mail.ru");

        // Then
        assertTrue(foundUserNotExistsEmail.isEmpty());
    }

    @Test
    void shouldExistsByEmail() {
        // Given
        User presavedUser = presaveUserInDB();

        // When
        boolean existsEmail = userRepository.existsByEmail(presavedUser.getEmail());
        boolean notExistsEmail = userRepository.existsByEmail("notExistsEmial@mail.ru");

        // Then
        assertTrue(existsEmail);
        assertFalse(notExistsEmail);
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUserBeforeDelete = userRepository.findById(presavedUser.getId());
        userRepository.deleteById(presavedUser.getId());
        Optional<User> foundUserAfterDelete = userRepository.findById(presavedUser.getId());

        // Then
        assertTrue(foundUserBeforeDelete.isPresent());
        assertTrue(foundUserAfterDelete.isEmpty());
    }

    @Test
    void shouldDeleteNotExistsUser() {
        // Given
        int id = 8;

        // When & Then
        userRepository.deleteById(id);
    }

    @Test
    void shouldUpdateUser(){
        // Given
        User presavedUser = presaveUserInDB();

        String nameAfterUpdate = "newName";
        String emailAfterUpdate = "newEmail@mail.ru";
        int ageAfterUpdate = 50;

        // When
        presavedUser.setName(nameAfterUpdate);
        presavedUser.setEmail(emailAfterUpdate);
        presavedUser.setAge(ageAfterUpdate);
        userRepository.save(presavedUser);
        Optional<User> foundUserAfterUpdate = userRepository.findById(presavedUser.getId());

        // Then
        assertTrue(foundUserAfterUpdate.isPresent());
        assertEquals(presavedUser.getId(), foundUserAfterUpdate.get().getId());
        assertEquals(nameAfterUpdate, foundUserAfterUpdate.get().getName());
        assertEquals(emailAfterUpdate, foundUserAfterUpdate.get().getEmail());
        assertEquals(ageAfterUpdate, foundUserAfterUpdate.get().getAge());
        assertEquals(presavedUser.getCreatedAt(), foundUserAfterUpdate.get().getCreatedAt());
    }

}
