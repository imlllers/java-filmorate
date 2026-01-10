package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновление пользователя");
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        log.info("Получение пользователя по ID");
        return userService.findById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление друга");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаление друга");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получение всех друзей");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получение общих друзей");
        return userService.getCommonFriends(id, otherId);
    }
}
