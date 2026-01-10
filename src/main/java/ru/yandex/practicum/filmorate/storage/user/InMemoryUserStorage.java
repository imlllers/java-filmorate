package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(
                    "Пользователь с id (" + user.getId() + ") не найден"
            );
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException(
                    "Пользователь с id (" + id + ") не найден"
            );
        }
        return user;
    }

    private long getNextId() {
        return users.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0) + 1;
    }
}

