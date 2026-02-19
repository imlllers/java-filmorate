package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addLike(Integer filmId, Integer userId) {
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    public void removeAllFilmLikes(Integer filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public void removeAllUserLikes(Integer userId) {
        String sql = "DELETE FROM likes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public List<Integer> getLikesUserIds(Integer filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    public int countLikes(Integer filmId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }
}
