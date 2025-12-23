package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final LocalDate firstDate = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(firstDate)) {
            log.warn("Ошибка: дата релиза фильма раньше 28 декабря 1895 года");
            throw new ValidationException(
                    "Дата релиза должна быть не раньше 28 декабря 1895 года"
            );
        }
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен");
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан ID фильма");
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = films.get(newFilm.getId());

        if (oldFilm == null) {
            log.warn("Ошибка: фильм с id={} не найден", newFilm.getId());
            throw new NotFoundException(
                    "Фильм с id (" + newFilm.getId() + ") не найден"
            );
        }
        if (newFilm.getName() != null) {
            if (newFilm.getName().isBlank()) {
                log.warn("Ошибка: пустое название фильма");
                throw new ValidationException("Название не может быть пустым");
            }
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            if (newFilm.getDescription().length() > 200) {
                log.warn("Ошибка: описание фильма больше 200 символов");
                throw new ValidationException("Максимальная длина описания 200 символов");
            }
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            if (newFilm.getReleaseDate().isBefore(firstDate)) {
                log.warn("Ошибка: дата релиза фильма раньше 28 декабря 1895 года");
                throw new ValidationException(
                        "Дата релиза должна быть не раньше 28 декабря 1895 года"
                );
            }
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != 0) {
            if (newFilm.getDuration() <= 0) {
                log.warn("Ошибка: продолжительность фильма не положительное число");
                throw new ValidationException(
                        "Продолжительность фильма должна быть положительным числом"
                );
            }
            oldFilm.setDuration(newFilm.getDuration());
        }

        log.info("Фильм успешно изменен");
        return oldFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private long getNextId() {
        return films.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0) + 1;
    }
}

