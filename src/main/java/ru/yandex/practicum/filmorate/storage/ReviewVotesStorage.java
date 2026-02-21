package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ReviewVotesStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final int LIKE = 1;
    private static final int DISLIKE = -1;

    public void addLike(Integer reviewId, Integer userId) {
        String deleteSql = "DELETE FROM review_votes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteSql, reviewId, userId);
        String insertSql = "INSERT INTO review_votes (user_id, review_id, vote) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, userId, reviewId, LIKE);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        String deleteSql = "DELETE FROM review_votes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteSql, reviewId, userId);
        String insertSql = "INSERT INTO review_votes (user_id, review_id, vote) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, userId, reviewId, DISLIKE);
    }

    public void removeLike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM review_votes WHERE review_id = ? AND user_id = ? AND vote = ?";
        jdbcTemplate.update(sql, reviewId, userId, LIKE);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM review_votes WHERE review_id = ? AND user_id = ? AND vote = ?";
        jdbcTemplate.update(sql, reviewId, userId, DISLIKE);
    }

    public void removeAllReviewVotes(Integer reviewId) {
        String sql = "DELETE FROM review_votes WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    public int getUseful(Integer reviewId) {
        String sql = "SELECT COALESCE(SUM(vote), 0) FROM review_votes WHERE review_id = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return result != null ? result : 0;
    }
}
