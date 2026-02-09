package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friends f " +
                "JOIN users u ON f.friend_id = u.id " +
                "WHERE f.user_id = ? " +
                "ORDER BY u.id";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friends f1 " +
                "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                "JOIN users u ON f1.friend_id = u.id " +
                "WHERE f1.user_id = ? AND f2.user_id = ? " +
                "ORDER BY u.id";
        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }
}
