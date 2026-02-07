package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<Integer> getFriendsIds(Integer userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId);
    }
}
