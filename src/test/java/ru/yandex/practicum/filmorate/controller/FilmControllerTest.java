package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    @Test
    void validFilm() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film result = controller.createFilm(film);
        assertNotNull(result.getId());
        assertEquals("Фильм", result.getName());
    }

    @Test
    void emptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020,2,23));
        film.setDuration(263);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void descriptionTooLong() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма".repeat(201));
        film.setReleaseDate(LocalDate.of(2020,2,23));
        film.setDuration(263);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void releaseDateTooEarly() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1890,7,18));
        film.setDuration(475);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void negativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020,2,23));
        film.setDuration(-147);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void updateNonExistentFilm() {
        Film film = new Film();
        film.setId(475L);
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020,2,23));
        film.setDuration(475);

        assertThrows(NotFoundException.class, () -> controller.updateFilm(film));
    }
}
