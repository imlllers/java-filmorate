package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

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
    public User findUserById(@PathVariable Integer id) {
        log.info("Получение пользователя по ID");
        return userService.findById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Добавление друга");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Удаление друга");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Получение всех друзей");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Получение общих друзей");
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Удаление пользователя с id={}", id);
        userService.deleteUser(id);
    }
}
