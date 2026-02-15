package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FriendsStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendsStorageTest {
    private final UserDbStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Test
    void addAndRemoveFriend() {
        User user = new User();
        user.setEmail("a@mail.ru");
        user.setLogin("a");
        user.setName("A");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = userStorage.create(user);

        User friend = new User();
        friend.setEmail("b@mail.ru");
        friend.setLogin("b");
        friend.setName("B");
        friend.setBirthday(LocalDate.of(1991, 1, 1));
        User createdFriend = userStorage.create(friend);

        friendsStorage.addFriend(created.getId(), createdFriend.getId());
        List<User> friends = friendsStorage.getFriends(created.getId());
        assertThat(friends).extracting(User::getId).contains(createdFriend.getId());

        friendsStorage.removeFriend(created.getId(), createdFriend.getId());
        List<User> afterRemove = friendsStorage.getFriends(created.getId());
        assertThat(afterRemove).extracting(User::getId).doesNotContain(createdFriend.getId());
    }
}
