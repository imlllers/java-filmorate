package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        //controller = new UserController();
    }

    @Test
    void validUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2006, 5, 23));

        User result = controller.createUser(user);

        assertNotNull(result.getId());
        assertEquals("testLogin", result.getLogin());
        assertEquals("Test User", result.getName());
    }

    @Test
    void emptyNameReplacedWithLogin() {
        User user = new User();
        user.setEmail("a@b.com");
        user.setLogin("login");
        user.setName("");
        user.setBirthday(LocalDate.of(2006, 5, 23));

        User result = controller.createUser(user);

        assertEquals("login", result.getName());
    }

    @Test
    void birthdayInFuture() {
        User user = new User();
        user.setEmail("a@b.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class,
                () -> controller.createUser(user));
    }

    @Test
    void updateNonExistentUser() {
        User user = new User();
        user.setId(999L);
        user.setEmail("a@b.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2006, 5, 23));

        assertThrows(NotFoundException.class,
                () -> controller.updateUser(user));
    }
}