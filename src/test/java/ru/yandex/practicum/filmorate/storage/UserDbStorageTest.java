package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void createAndFindById() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.create(user);
        User found = userStorage.findById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getEmail()).isEqualTo("user@mail.ru");
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.create(user);
        created.setName("Updated");
        User updated = userStorage.update(created);

        assertThat(updated.getName()).isEqualTo("Updated");
    }

    @Test
    void findAllUsers() {
        User user = new User();
        user.setEmail("user2@mail.ru");
        user.setLogin("login2");
        user.setName("User2");
        user.setBirthday(LocalDate.of(1992, 2, 2));
        userStorage.create(user);

        Collection<User> users = userStorage.findAll();
        assertThat(users).isNotEmpty();
    }
}
