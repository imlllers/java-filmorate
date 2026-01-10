package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        validateUser(user);
        return userStorage.update(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public void addFriend(Long id, Long friendId) {
        User user = findById(id);
        User friend = findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = findById(id);
        User friend = findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(Long userId) {
        User user = findById(userId);

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(findById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = findById(userId);
        User other = findById(otherId);

        List<User> common = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            if (other.getFriends().contains(friendId)) {
                common.add(findById(friendId));
            }
        }
        return common;
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
