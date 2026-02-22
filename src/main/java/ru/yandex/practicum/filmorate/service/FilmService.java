package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final LikesStorage likesStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;
    private static final LocalDate FIRST_DATE = LocalDate.of(1895, 12, 28);

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        validateFilm(film);
        return filmStorage.update(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Integer id) {
        return filmStorage.findById(id);
    }

    public Collection<Film> findByName(String query, String by) {
        return filmStorage.findByNameAndDirector(query, by);
    }

    public void addLike(Integer id, Integer userId) {
        findById(id);
        userService.findById(userId);
        likesStorage.addLike(id, userId);

        eventService.createEvent(userId, EventType.LIKE, Operation.ADD, id);
    }

    public void removeLike(Integer id, Integer userId) {
        findById(id);
        userService.findById(userId);
        likesStorage.removeLike(id, userId);

        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, id);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.findTop(count);
    }

    public List<Film> getPopularFilmsByGenreAndYear(int genreId, int year, int count) {
        Genre genre = genreStorage.findById(genreId);
        return filmStorage.findPopularFilmsByGenreAndYear(genre, year, count);
    }

    public List<Film> getPopularFilmsByYear(int year, int count) {
        return filmStorage.findPopularFilmsByYear(year, count);
    }

    public List<Film> getPopularFilmsByGenre(int genreId, int count) {
        Genre genre = genreStorage.findById(genreId);
        return filmStorage.findPopularFilmsByGenre(genre, count);
    }

    public Film addDirectorToFilm(Integer filmId, Integer directorId) {
        Film film = filmStorage.findById(filmId);
        directorStorage.findById(directorId);
        directorStorage.addDirectorToFilm(filmId, directorId);
        return filmStorage.findById(filmId);
    }

    public Film removeAllDirectorsFromFilm(Integer filmId) {
        Film film = filmStorage.findById(filmId);
        directorStorage.removeAllDirectorsFromFilm(filmId);
        return film;
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userService.findById(userId);
        userService.findById(friendId);
        return filmStorage.findCommonFilms(userId, friendId);
    }

    @Transactional
    public void deleteFilm(Integer id) {
        findById(id);

        likesStorage.removeAllFilmLikes(id);
        filmStorage.removeAllGenres(id);

        filmStorage.delete(id);

        eventService.createEvent(null, EventType.FILM, Operation.REMOVE, id);
    }

    public Film removeDirectorFromFilm(Integer filmId, Integer directorId) {
        if (filmId == null || filmId <= 0) {
            throw new ValidationException("Некорректный ID фильма");
        }
        if (directorId == null || directorId <= 0) {
            throw new ValidationException("Некорректный ID режиссёра");
        }

        Film film = filmStorage.findById(filmId);
        directorStorage.findById(directorId);
        return filmStorage.removeDirectorFromFilm(filmId, directorId);
    }

    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        if (directorId == null || directorId <= 0) {
            throw new ValidationException("Некорректный ID режиссёра");
        }
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new ValidationException("Неуказан параметр sortBy");
        }

        directorStorage.findById(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_DATE)) {
            throw new ValidationException(
                    "Дата релиза должна быть не раньше 28 декабря 1895 года"
            );
        }
    }
}