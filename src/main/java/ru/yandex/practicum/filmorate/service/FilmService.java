package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikesStorage likesStorage;
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

    public Collection<Film> findByName(String query) {
        return filmStorage.findByName(query);
    }

    public void addLike(Integer id, Integer userId) {
        findById(id);
        userService.findById(userId);
        likesStorage.addLike(id, userId);
    }

    public void removeLike(Integer id, Integer userId) {
        findById(id);
        userService.findById(userId);
        likesStorage.removeLike(id, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.findTop(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_DATE)) {
            throw new ValidationException(
                    "Дата релиза должна быть не раньше 28 декабря 1895 года"
            );
        }
    }
}
