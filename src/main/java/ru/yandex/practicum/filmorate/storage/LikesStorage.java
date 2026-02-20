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

    public List<Integer> getLikesUserIds(Integer filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    public int countLikes(Integer filmId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    public int findSimilarUser(Integer userId) {
        String sql = "SELECT l2.user_id " +
                "FROM likes l1 " +
                "JOIN likes l2 ON l1.film_id = l2.film_id " +
                "WHERE l1.user_id = ? AND l2.user_id <> ? " +
                "GROUP BY l2.user_id " +
                "ORDER BY COUNT(*) DESC, l2.user_id ASC " +
                "LIMIT 1";

        List<Integer> userIds = jdbcTemplate.queryForList(sql, Integer.class, userId, userId);
        if (userIds.isEmpty()) {
            return -1;
        }

        return userIds.getFirst();
    }

    public List<Integer> findRecommendedFilm(int userId, int similarUserId) {
        if (similarUserId == -1) {
            return List.of();
        }

        String sql = "SELECT similar_like.film_id " +
                "FROM likes AS similar_like " +
                "WHERE similar_like.user_id = ? " +
                "AND NOT EXISTS ( " +
                "SELECT current_like.film_id " +
                "FROM likes AS current_like " +
                "WHERE current_like.user_id = ? " +
                "AND current_like.film_id = similar_like.film_id " +
                ") " +
                "ORDER BY similar_like.film_id; ";

        return jdbcTemplate.queryForList(sql, Integer.class, similarUserId, userId);
    }

    public void removeAllFilmLikes(Integer filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public void removeAllUserLikes(Integer userId) {
        String sql = "DELETE FROM likes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}
