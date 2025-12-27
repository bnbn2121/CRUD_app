package com.aston.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserDtoIn {

    @Schema(description = "Имя пользователя")
    private String name;

    @Schema(description = "Адрес электнонной почты пользователя")
    private String email;

    @Schema(description = "Возраст пользователя")
    private Integer age;

    public UserDtoIn() {
    }

    public UserDtoIn(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
