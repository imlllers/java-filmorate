package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    void delete(Integer id);

    Review findById(Integer id);

    List<Review> findByFilmId(Integer filmId, int count);

    List<Review> findAll(int count);
}
