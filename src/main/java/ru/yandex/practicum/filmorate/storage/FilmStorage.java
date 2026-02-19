package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film findById(Integer id);

    List<Film> findTop(int count);

    void delete(Integer id);

    void removeAllGenres(Integer filmId);
}
