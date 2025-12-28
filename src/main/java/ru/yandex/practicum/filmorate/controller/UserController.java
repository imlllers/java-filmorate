package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя заменено на логин");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка: дата рождения указана в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь успешно создан");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Ошибка: не указан ID пользователя");
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());

        if (oldUser == null) {
            log.warn("Ошибка: пользователь с id={} не найден", newUser.getId());
            throw new NotFoundException(
                    "Пользователь с id (" + newUser.getId() + ") не найден"
            );
        }
        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                log.warn("Ошибка: некорректный email");
                throw new ValidationException("Электронная почта некорректна");
            }
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            if (newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                log.warn("Ошибка: логин пустой или содержит пробелы");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            }
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            if (newUser.getName().isBlank()) {
                log.info("Имя пользователя заменено на логин");
                oldUser.setName(oldUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
        }
        if (newUser.getBirthday() != null) {
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("Ошибка: дата рождения указана в будущем");
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            oldUser.setBirthday(newUser.getBirthday());
        }

        log.info("Пользователь успешно изменен");
        return oldUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private long getNextId() {
        return users.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0) + 1;
    }
}

