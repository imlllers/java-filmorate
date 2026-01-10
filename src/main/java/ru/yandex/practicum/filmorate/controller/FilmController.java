package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

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
    public Film findFilmById(@PathVariable Long id) {
        log.info("Получение фильма по ID");
        return filmService.findById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление лайка");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление лайка");
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получение топа фильмов");
        return filmService.getTopFilms(count);
    }
}


