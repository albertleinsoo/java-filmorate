package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;

    @Autowired
    public ReviewService(ReviewDbStorage reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    public Review create(Review review) {
        log.trace("В сервис {} получен запрос на создание отзыва {}", this.getClass(), review.toString());
        if (!reviewDbStorage.checkUser(review.getUserId())) {
            throw new UserIdUnknownException(review.getUserId());
        }
        if (!reviewDbStorage.checkFilm(review.getFilmId())) {
            //TODO Find out, why "String message" expected?
            throw new FilmIdUnknownException(String.valueOf(review.getFilmId()));
        }
        if (reviewDbStorage.checkReview(review)) {
            throw new ReviewAlreadyExistsException(review);
        }
        return reviewDbStorage.create(review);
    }

    public Review get(long id) {
        log.trace("В сервис {} получен запрос на получение отзыва с id {}", this.getClass(), id);
        if (!reviewDbStorage.checkReview(id)) {
            throw new ReviewNotExistsException(id);
        }
        return reviewDbStorage.get(id);
    }

    public Review update(Review review) {
        log.trace("В сервис {} получен запрос на обновление отзыва {}", this.getClass(), review.toString());
        if (!reviewDbStorage.checkReview(review)) {
            throw new ReviewNotExistsException(review);
        }
        return reviewDbStorage.update(review);
    }

    public boolean delete(long id) {
        log.trace("В сервис {} получен запрос на удаление отзыва с id {}", this.getClass(), id);
        if (!reviewDbStorage.checkReview(id)) {
            throw new ReviewNotExistsException(id);
        }
        return reviewDbStorage.delete(id);
    }

    public List<Review> getAll() {
        log.trace("В сервис {} получен запрос на получение всех отзывов", this.getClass());
        return reviewDbStorage.getAll();
    }

    public List<Review> getReviewsByFilm(long filmId, int count) {
        log.trace("В сервис {} получен запрос на получение отзывов о фильме id {} в количестве {}",
                this.getClass(),
                filmId,
                count);
        if (!reviewDbStorage.checkFilm(filmId)) {
            //TODO Find out, why "String message" expected?
            throw new FilmIdUnknownException(String.valueOf(filmId));
        }
        return reviewDbStorage.getReviewByFilm(filmId, count);
    }

    public void addLike(long id, long userId) {
        log.trace("В сервис {} получен запрос на добавление like к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewDbStorage.checkReview(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!reviewDbStorage.checkUser(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (!reviewDbStorage.checkLike(id, userId, true)) {
            reviewDbStorage.addLike(id, userId);
            Review review = reviewDbStorage.get(id);
            review.setUseful(review.getUseful() + 1);
            reviewDbStorage.update(review);
        } else {
            throw new LikeAlreadyExistsException(id, userId);
        }
    }

    public void addDislike(long id, long userId) {
        log.trace("В сервис {} получен запрос на добавление dislike к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewDbStorage.checkReview(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!reviewDbStorage.checkUser(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (!reviewDbStorage.checkLike(id, userId, false)) {
            reviewDbStorage.addDislike(id, userId);
            Review review = reviewDbStorage.get(id);
            review.setUseful(review.getUseful() - 1);
            reviewDbStorage.update(review);
        } else {
            throw new DislikeAlreadyExistsException(id, userId);
        }
    }

    public void deleteLike(long id, long userId) {
        log.trace("В сервис {} получен запрос на удаление like к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewDbStorage.checkReview(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!reviewDbStorage.checkUser(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (reviewDbStorage.checkLike(id, userId, true)) {
            reviewDbStorage.deleteLike(id, userId);
            Review review = reviewDbStorage.get(id);
            review.setUseful(review.getUseful() - 1);
            reviewDbStorage.update(review);
        } else {
            throw new LikeNotExistsException(id, userId);
        }
    }

    public void deleteDislike(long id, long userId) {
        log.trace("В сервис {} получен запрос на удаление dislike к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewDbStorage.checkReview(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!reviewDbStorage.checkUser(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (reviewDbStorage.checkLike(id, userId, false)) {
            reviewDbStorage.deleteDislike(id, userId);
            Review review = reviewDbStorage.get(id);
            review.setUseful(review.getUseful() + 1);
            reviewDbStorage.update(review);
        } else {
            throw new DislikeNotExistsException(id, userId);
        }
    }
}
