package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper = new ReviewRowMapper();

    private static final String BASE_SELECT = """
            SELECT r.id, r.content, r.is_positive, r.film_id, r.user_id,
                   COALESCE(SUM(rv.vote), 0) AS useful
            FROM reviews r
            LEFT JOIN review_votes rv ON r.id = rv.review_id
            """;

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, film_id, user_id) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getFilmId());
            ps.setInt(4, review.getUserId());
            return ps;
        }, keyHolder);

        review.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return findById(review.getId());
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";

        int updated = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Отзыв с id=" + review.getId() + " не найден");
        }

        return findById(review.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted == 0) {
            throw new NotFoundException("Отзыв с id=" + id + " не найден");
        }
    }

    @Override
    public Review findById(Integer id) {
        String sql = BASE_SELECT + " WHERE r.id = ? GROUP BY r.id, r.content, r.is_positive, r.film_id, r.user_id";

        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review r = reviewRowMapper.mapRow(rs, rowNum);
            r.setUseful(rs.getInt("useful"));
            return r;
        }, id);

        if (reviews.isEmpty()) {
            throw new NotFoundException("Отзыв с id=" + id + " не найден");
        }

        return reviews.getFirst();
    }

    @Override
    public List<Review> findByFilmId(Integer filmId, int count) {
        String sql = BASE_SELECT + " WHERE r.film_id = ? GROUP BY r.id, r.content, r.is_positive, r.film_id, r.user_id " +
                "ORDER BY useful DESC, r.id ASC LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review r = reviewRowMapper.mapRow(rs, rowNum);
            r.setUseful(rs.getInt("useful"));
            return r;
        }, filmId, count);
    }

    @Override
    public List<Review> findAll(int count) {
        String sql = BASE_SELECT + " GROUP BY r.id, r.content, r.is_positive, r.film_id, r.user_id " +
                "ORDER BY useful DESC, r.id ASC LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review r = reviewRowMapper.mapRow(rs, rowNum);
            r.setUseful(rs.getInt("useful"));
            return r;
        }, count);
    }
}
