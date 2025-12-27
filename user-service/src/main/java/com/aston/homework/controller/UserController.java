package com.aston.homework.controller;

import com.aston.homework.dto.EventName;
import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.service.KafkaProducerService;
import com.aston.homework.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/app")
@Tag(name = "User-service API", description = "CRUD операции с пользователями")
@CircuitBreaker(name = "userService")
public class UserController {
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;

    public UserController(UserService userService, KafkaProducerService kafkaProducerService) {
        this.userService = userService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<EntityModel<UserDtoOut>> getUserById(@Parameter(description = "ID пользователя") @PathVariable int id) {
        UserDtoOut userDtoOut = userService.getUserById(id);
        EntityModel<UserDtoOut> response = EntityModel.of(userDtoOut);
        response.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).updateUserById(id, null)).withRel("update"));
        response.add(linkTo(methodOn(UserController.class).removeUserById(id)).withRel("delete"));
        System.out.println("гет метод успешный");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь создан")
    @ApiResponse(responseCode = "409", description = "Невозможно создать пользователя с заданными параметрами")
    public ResponseEntity<EntityModel<UserDtoOut>> addUser(@Parameter(description = "Данные нового пользователя") @RequestBody UserDtoIn userDtoIn) {
        UserDtoOut userDtoOut = userService.addUser(userDtoIn);
        kafkaProducerService.sendEvent(EventName.CREATE, userDtoOut);
        EntityModel<UserDtoOut> response = EntityModel.of(userDtoOut);
        response.add(linkTo(methodOn(UserController.class).getUserById(userDtoOut.getId())).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).updateUserById(userDtoOut.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(UserController.class).removeUserById(userDtoOut.getId())).withRel("delete"));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя с заданным ID")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлен")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "409", description = "Невозможно обновить пользователя с заданными параметрами")
    public ResponseEntity<EntityModel<UserDtoOut>> updateUserById(@Parameter(description = "ID пользователя") @PathVariable int id,
                                            @Parameter(description = "Данные пользователя для обновления") @RequestBody UserDtoIn userDtoIn) {
        UserDtoOut userDtoOut = userService.updateUserById(id, userDtoIn);
        EntityModel<UserDtoOut> response = EntityModel.of(userDtoOut);
        response.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).updateUserById(id, null)).withRel("update"));
        response.add(linkTo(methodOn(UserController.class).removeUserById(id)).withRel("delete"));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь удален")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<EntityModel<Map<String, Boolean>>> removeUserById(@Parameter(description = "ID пользователя") @PathVariable int id) {
        UserDtoOut userDtoOut = userService.getUserById(id);
        boolean isDelete = userService.deleteUser(id);
        kafkaProducerService.sendEvent(EventName.DELETE, userDtoOut);
        EntityModel<Map<String, Boolean>> response = EntityModel.of(Map.of("delete result", isDelete));
        response.add(linkTo(methodOn(UserController.class).addUser(null)).withRel("create"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ex")
    public ResponseEntity<EntityModel<UserDtoOut>> testMethod() {
        throw new RuntimeException("это ошибка какая-то");
    }
}
