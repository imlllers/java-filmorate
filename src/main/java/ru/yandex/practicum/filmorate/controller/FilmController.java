package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Добавление лайка");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Удаление лайка");
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsByGenreAndYear(@RequestParam(required = false) Integer genreId,
                                                    @RequestParam(required = false) Integer year,
                                                    @RequestParam(defaultValue = "10") int count) {
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
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.info("Получение общих фильмов пользователей {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @PutMapping("/{id}/directors/{directorId}")
    public Film addDirectorToFilm(@PathVariable Integer id, @PathVariable Integer directorId) {
        log.info("Добавление режиссёра {} к фильму {}", directorId, id);
        return filmService.addDirectorToFilm(id, directorId);
    }

    @DeleteMapping("/{id}/directors/{directorId}")
    public Film removeDirectorFromFilm(@PathVariable Integer id, @PathVariable Integer directorId) {
        log.info("Удаление режиссёра {} из фильма {}", directorId, id);
        return filmService.removeDirectorFromFilm(id, directorId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        log.info("Удаление фильма с id={}", id);
        filmService.deleteFilm(id);
    }
}
