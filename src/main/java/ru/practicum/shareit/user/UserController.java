package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя по его id");
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос на создание пользователя");
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto user) {
        log.info("Получен запрос на обновление пользователя");
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя");
        return userService.deleteUser(userId);
    }
}
