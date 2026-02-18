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

    @GetMapping("/search")
    public Collection<Film> getFilmsByName(@RequestParam String query) {
        log.info("Поиск фильмов по названию");
        return filmService.findByName(query);
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
    public List<Film> getPopularFilmsByGenreAndYear(@RequestParam(required = false)  Integer genreId,
                                                    @RequestParam(required = false) Integer year,
                                                    @RequestParam(defaultValue = "10") int count) {
       if (year != null && genreId != null) {
           return filmService.getPopularFilmsByGenreAndYear(genreId, year, count);
       } else if (genreId != null) {
           return filmService.getPopularFilmsByGenre(genreId, count);
       } else if (year != null) {
           return filmService.getPopularFilmsByYear(year, count);
       } else {
           return filmService.getTopFilms(count); // в случае если и поле year и genreId отсутствуют, выведется 10 популярных фильмов
       }
    }
}

