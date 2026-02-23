package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.*;

@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper = new FilmRowMapper();
    private final GenreRowMapper genreRowMapper = new GenreRowMapper();
    private final DirectorRowMapper directorRowMapper = new DirectorRowMapper();

    @Override
    public Film create(Film film) {
        validateMpaAndGenres(film);
        String sql = "INSERT INTO films (name, description, releaseDate, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        saveGenres(film.getId(), film.getGenres());
        saveDirectors(film.getId(), film.getDirectors());

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) {
        validateMpaAndGenres(film);
        String sql = "UPDATE films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=? WHERE id=?";

        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film.getId(), film.getGenres());

        jdbcTemplate.update("DELETE FROM films_directors WHERE film_id = ?", film.getId());
        saveDirectors(film.getId(), film.getDirectors());

        return findById(film.getId());
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        fillGenresForFilms(films);
        fillLikesForFilms(films);
        fillDirectorsForFilms(films);

        return films;
    }

    @Override
    public Film findById(Integer id) {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }

        Film film = films.getFirst();
        fillGenres(film);
        fillLikes(film);
        fillDirectors(film);
        return film;
    }

    @Override
    public List<Film> findTop(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, m.name " +
                "ORDER BY COUNT(l.user_id) DESC, f.id ASC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        fillGenresForFilms(films);
        fillLikesForFilms(films);
        fillDirectorsForFilms(films);

        return films;
    }

    @Override
    public List<Film> findByNameAndDirector(String query, String by) {
        String search = "%" + query + "%";

        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "LEFT JOIN films_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.id ";

        switch (by) {
            case "title" -> sql += "WHERE LOWER(f.name) LIKE LOWER(?) ";
            case "director" -> sql += "WHERE LOWER(d.name) LIKE LOWER(?) ";
            case "director,title", "title,director" ->
                    sql += "WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?) ";
            case null, default -> throw new IllegalArgumentException("Unknown by: " + by);
        }

        sql += "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, m.name " +
                "ORDER BY COUNT(l.user_id) DESC, f.id ASC";

        List<Film> films;

        if (by.contains(",")) {
            films = jdbcTemplate.query(sql, filmRowMapper, search, search);
        } else {
            films = jdbcTemplate.query(sql, filmRowMapper, search);
        }

        fillGenresForFilms(films);
        fillLikesForFilms(films);
        fillDirectorsForFilms(films);

        return films;
    }

    @Override
    public List<Film> findPopularFilmsByGenreAndYear(Genre genre, int year, int count) {
        String sql = "SELECT f.*, m.name as mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE fg.genre_id = ? AND YEAR(f.releaseDate) = ? " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, m.id, m.name " +
                "ORDER BY likes_count DESC, f.id ASC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, genre.getId(), year, count);
        fillGenresForFilms(films);
        fillDirectorsForFilms(films);
        return films;
    }

    @Override
    public List<Film> findPopularFilmsByGenre(Genre genre, int count) {
        String sql = "SELECT f.*, m.name as mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, m.id, m.name " +
                "ORDER BY likes_count DESC, f.id ASC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, genre.getId(), count);
        fillGenresForFilms(films);
        fillDirectorsForFilms(films);
        return films;
    }

    @Override
    public List<Film> findPopularFilmsByYear(int year, int count) {
        String sql = "SELECT f.*, m.name as mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE YEAR(f.releaseDate) = ? " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, m.id, m.name " +
                "ORDER BY likes_count DESC, f.id ASC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, year, count);
        fillGenresForFilms(films);
        fillDirectorsForFilms(films);
        return films;
    }

    @Override
    public List<Film> findCommonFilms(Integer userId, Integer friendId) {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "JOIN likes l1 ON f.id = l1.film_id AND l1.user_id = ? " +
                "JOIN likes l2 ON f.id = l2.film_id AND l2.user_id = ? " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, m.name " +
                "ORDER BY COUNT(l.user_id) DESC, f.id ASC";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, userId, friendId);
        fillGenresForFilms(films);
        fillDirectorsForFilms(films);
        return films;
    }

    @Override
    public Film removeDirectorFromFilm(Integer filmId, Integer directorId) {
        String sql = "DELETE FROM films_directors WHERE film_id = ? AND director_id = ?";
        jdbcTemplate.update(sql, filmId, directorId);
        return findById(filmId);
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        String orderBy = sortBy.equals("year") ? "f.releaseDate" : "likes_count DESC";

        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
                "f.mpa_id, m.name AS mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "JOIN films_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, m.name " +
                "ORDER BY " + orderBy;

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, directorId);
        fillGenresForFilms(films);
        fillDirectorsForFilms(films);
        return films;
    }

    private void fillGenres(Film film) {
        String sql = "SELECT g.id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.id";

        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void fillGenresForFilms(Collection<Film> films) {
        for (Film film : films) {
            fillGenres(film);
        }
    }

    private void fillLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ? ORDER BY user_id";
        List<Integer> userIds = jdbcTemplate.queryForList(sql, Integer.class, film.getId());
        film.setLikes(new LinkedHashSet<>(userIds));
    }

    private void fillLikesForFilms(Collection<Film> films) {
        for (Film film : films) {
            fillLikes(film);
        }
    }

    private void fillDirectors(Film film) {
        String sql = "SELECT d.id, d.name FROM directors d " +
                "JOIN films_directors fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ? ORDER BY d.id";

        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper, film.getId());
        film.setDirectors(new LinkedHashSet<>(directors));
    }

    private void fillDirectorsForFilms(Collection<Film> films) {
        for (Film film : films) {
            fillDirectors(film);
        }
    }

    private void saveGenres(Integer filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    private void saveDirectors(Integer filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";

        for (Director director : directors) {
            jdbcTemplate.update(sql, filmId, director.getId());
        }
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getMpa() != null) {
            Integer mpaId = film.getMpa().getId();
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM mpa WHERE id = ?",
                    Integer.class,
                    mpaId
            );
            if (count == null || count == 0) {
                throw new NotFoundException("Рейтинг MPA с id=" + mpaId + " не найден");
            }
        }

        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }

        for (Genre genre : genres) {
            Integer genreId = genre.getId();
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM genres WHERE id = ?",
                    Integer.class,
                    genreId
            );
            if (count == null || count == 0) {
                throw new NotFoundException("Жанр с id=" + genreId + " не найден");
            }
        }
    }

    private void loadLikesAndGenres(Film film) {
        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> likes = jdbcTemplate.queryForList(likesSql, Integer.class, film.getId());
        film.setLikes(new HashSet<>(likes));

        String genresSql = "SELECT g.* FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genresSql, new GenreRowMapper(), film.getId());
        film.setGenres(new HashSet<>(genres));
    }

    @Override
    public void removeAllGenres(Integer filmId) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
    }
}
