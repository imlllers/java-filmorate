package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final LikesStorage likesStorage;
    private final FilmStorage filmStorage;

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

    public User findById(Integer id) {
        return userStorage.findById(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        findById(id);
        findById(friendId);
        friendsStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        findById(id);
        findById(friendId);
        friendsStorage.removeFriend(id, friendId);
    }

    public List<User> getFriends(Integer userId) {
        findById(userId);
        return friendsStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        findById(userId);
        findById(otherId);
        return friendsStorage.getCommonFriends(userId, otherId);
    }

    public List<Film> getRecommendations(Integer userId) {
        findById(userId);
        int similarUserId = likesStorage.findSimilarUser(userId);
        List<Integer> filmIds = likesStorage.findRecommendedFilm(userId, similarUserId);

        if (filmIds.isEmpty()) {
            return List.of();
        }

        return filmIds.stream()
                .map(filmStorage::findById)
                .toList();
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
