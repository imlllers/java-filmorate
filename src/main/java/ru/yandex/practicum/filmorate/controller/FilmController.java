package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма");
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        log.info("Изменение фильм");
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable Integer id) {
        log.info("Получение фильма по ID");
        return filmService.findById(id);
    }

    @GetMapping("/search")
    public Collection<Film> search(
            @RequestParam String query,
            @RequestParam String by) {

        log.info("Поиск фильмов по названию и режиссеру");
        return filmService.findByName(query, by);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable
            @NotNull(message = "ID фильма не может быть пустым")
            @Positive(message = "ID фильма должен быть положительным")
            Integer id,

            @PathVariable
            @NotNull(message = "ID пользователя не может быть пустым")
            @Positive(message = "ID пользователя должен быть положительным")
            Integer userId) {

        log.info("Добавление лайка");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable
            @NotNull(message = "ID фильма не может быть пустым")
            @Positive(message = "ID фильма должен быть положительным")
            Integer id,

            @PathVariable
            @NotNull(message = "ID пользователя не может быть пустым")
            @Positive(message = "ID пользователя должен быть положительным")
            Integer userId) {

        log.info("Удаление лайка");
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsByGenreAndYear(
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "10")
            @Positive(message = "Количество должно быть положительным")
            int count) {

        if (year != null && genreId != null) {
            return filmService.getPopularFilmsByGenreAndYear(genreId, year, count);
        } else if (genreId != null) {
            return filmService.getPopularFilmsByGenre(genreId, count);
        } else if (year != null) {
            return filmService.getPopularFilmsByYear(year, count);
        } else {
            return filmService.getTopFilms(count);
        }
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam
            @NotNull(message = "ID пользователя не может быть пустым")
            @Positive(message = "ID пользователя должен быть положительным")
            Integer userId,

            @RequestParam
            @NotNull(message = "ID друга не может быть пустым")
            @Positive(message = "ID друга должен быть положительным")
            Integer friendId) {

        log.info("Получение общих фильмов пользователей {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @PutMapping("/{id}/directors/{directorId}")
    public Film addDirectorToFilm(
            @PathVariable
            @NotNull(message = "ID фильма не может быть пустым")
            @Positive(message = "ID фильма должен быть положительным")
            Integer id,

            @PathVariable
            @NotNull(message = "ID режиссёра не может быть пустым")
            @Positive(message = "ID режиссёра должен быть положительным")
            Integer directorId) {

        log.info("Добавление режиссёра {} к фильму {}", directorId, id);
        return filmService.addDirectorToFilm(id, directorId);
    }

    @DeleteMapping("/{id}/directors/{directorId}")
    public Film removeDirectorFromFilm(
            @PathVariable
            @NotNull(message = "ID фильма не может быть пустым")
            @Positive(message = "ID фильма должен быть положительным")
            Integer id,

            @PathVariable
            @NotNull(message = "ID режиссёра не может быть пустым")
            @Positive(message = "ID режиссёра должен быть положительным")
            Integer directorId) {

        log.info("Удаление режиссёра {} из фильма {}", directorId, id);
        return filmService.removeDirectorFromFilm(id, directorId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(
            @PathVariable
            @NotNull(message = "ID фильма не может быть пустым")
            @Positive(message = "ID фильма должен быть положительным")
            Integer id) {

        log.info("Удаление фильма с id={}", id);
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/directors")
    public Film removeAllDirectorsFromFilm(
            @PathVariable
            @NotNull(message = "ID фильма не может быть пустым")
            @Positive(message = "ID фильма должен быть положительным")
            Integer id) {

        log.info("Удаление всех режиссёров из фильма {}", id);
        return filmService.removeAllDirectorsFromFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(
            @PathVariable
            @NotNull(message = "ID режиссёра не может быть пустым")
            @Positive(message = "ID режиссёра должен быть положительным")
            Integer directorId,

            @RequestParam(defaultValue = "year")
            String sortBy) {

        log.info("Получение фильмов режиссёра {} с сортировкой по {}", directorId, sortBy);
        return filmService.getFilmsByDirector(directorId, sortBy);
    }
}