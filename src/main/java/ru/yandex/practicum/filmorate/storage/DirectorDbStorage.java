package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper = new DirectorRowMapper();

    @Override
    public List<Director> findAll() {
        String sql = "SELECT id, name FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Director findById(Integer id) {
        String sql = "SELECT id, name FROM directors WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper, id);
        if (directors.isEmpty()) {
            throw new NotFoundException("Режиссёр с id=" + id + " не найден");
        }
        return directors.getFirst();
    }

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (updated == 0) {
            throw new NotFoundException("Режиссёр с id=" + director.getId() + " не найден");
        }

        return findById(director.getId());
    }

    @Override
    public void delete(Integer id) {
        String deleteFilmDirectorsSql = "DELETE FROM films_directors WHERE director_id = ?";
        jdbcTemplate.update(deleteFilmDirectorsSql, id);

        String sql = "DELETE FROM directors WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);

        if (deleted == 0) {
            throw new NotFoundException("Режиссёр с id=" + id + " не найден");
        }
    }

    @Override
    public void addDirectorToFilm(Integer filmId, Integer directorId) {
        String sql = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, directorId);
    }

    @Override
    public void removeAllDirectorsFromFilm(Integer filmId) {
        String sql = "DELETE FROM films_directors WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}