package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewVotesStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewVotesStorage reviewVotesStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review create(Review review) {
        validateReview(review);
        filmService.findById(review.getFilmId());
        userService.findById(review.getUserId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        if (review.getId() == null) {
            throw new ValidationException("Id отзыва должен быть указан");
        }
        validateReview(review);
        reviewStorage.findById(review.getId());
        return reviewStorage.update(review);
    }

    public void delete(Integer id) {
        reviewStorage.findById(id);
        reviewStorage.delete(id);
    }

    public Review findById(Integer id) {
        return reviewStorage.findById(id);
    }

    public List<Review> findReviews(Integer filmId, int count) {
        if (filmId != null) {
            filmService.findById(filmId);
            return reviewStorage.findByFilmId(filmId, count);
        }
        return reviewStorage.findAll(count);
    }

    public void addLike(Integer reviewId, Integer userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);
        reviewVotesStorage.addLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);
        reviewVotesStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Integer reviewId, Integer userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);
        reviewVotesStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);
        reviewVotesStorage.removeDislike(reviewId, userId);
    }

    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Содержимое отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва должен быть указан");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("Идентификатор фильма должен быть указан");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("Идентификатор пользователя должен быть указан");
        }
    }
}
