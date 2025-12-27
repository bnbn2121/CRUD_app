package com.aston.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class UserDtoOut {

    @Schema(description = "ID пользователя")
    private int id;

    @Schema(description = "Имя пользователя")
    private String name;

    @Schema(description = "Адрес электнонной почты пользователя")
    private String email;

    @Schema(description = "Возраст пользователя")
    private int age;

    @Schema(description = "Дата регистрации пользователя")
    private LocalDateTime createdAt;

    public UserDtoOut() {
    }

    public UserDtoOut(int id, String name, String email, int age, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserDtoOut{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                '}';
    }
}
