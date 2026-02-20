package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film findById(Integer id);

    List<Film> findTop(int count);

    List<Film> findByName(String name);

    List<Film> findPopularFilmsByGenre(Genre genre, int count);

    List<Film> findPopularFilmsByYear(int year, int count);

    List<Film> findPopularFilmsByGenreAndYear(Genre genre, int year, int count);

    List<Film> findCommonFilms(Integer userId, Integer friendId);

    void delete(Integer id);

    void removeAllGenres(Integer filmId);
}
