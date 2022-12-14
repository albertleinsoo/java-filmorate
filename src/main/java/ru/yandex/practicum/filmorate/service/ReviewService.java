package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventService eventService;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public Review create(Review review) {
        log.trace("В сервис {} получен запрос на создание отзыва {}", this.getClass(), review.toString());
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new NullUserOrFilmIdException();
        }
        if (!userStorage.isUserExists(review.getUserId())) {
            throw new UserIdUnknownException(review.getUserId());
        }
        if (!filmStorage.isFilmExists(review.getFilmId())) {
            throw new FilmIdUnknownException(String.valueOf(review.getFilmId()));
        }
        if (reviewStorage.isReviewExists(review)) {
            throw new ReviewAlreadyExistsException(review);
        }

        var createdReview = reviewStorage.create(review);
        eventService.createAddReviewEvent(createdReview.getUserId(), createdReview.getReviewId());
        return createdReview;
    }

    public Review get(long id) {
        log.trace("В сервис {} получен запрос на получение отзыва с id {}", this.getClass(), id);
        if (!reviewStorage.isReviewExists(id)) {
            throw new ReviewNotExistsException(id);
        }
        return reviewStorage.get(id);
    }

    public Review update(Review review) {
        log.trace("В сервис {} получен запрос на обновление отзыва {}", this.getClass(), review.toString());
        if (!reviewStorage.isReviewExists(review.getReviewId())) {
            throw new ReviewNotExistsException(review);
        }

        reviewStorage.update(review);

        var updatedReview = reviewStorage.get(review.getReviewId());
        eventService.createUpdateReviewEvent(updatedReview.getUserId(), updatedReview.getReviewId());
        return updatedReview;
    }

    public boolean delete(long id) {
        log.trace("В сервис {} получен запрос на удаление отзыва с id {}", this.getClass(), id);
        if (!reviewStorage.isReviewExists(id)) {
            throw new ReviewNotExistsException(id);
        }

        var deletedReview = reviewStorage.get(id);
        var isDeleted = reviewStorage.delete(id);
        if (isDeleted) {
            eventService.createRemoveReviewEvent(deletedReview.getUserId(), deletedReview.getReviewId());
        }
        return isDeleted;
    }

    public List<Review> getAll() {
        log.trace("В сервис {} получен запрос на получение всех отзывов", this.getClass());
        return reviewStorage.getAll();
    }

    public List<Review> getReviewsByFilm(long filmId, int count) {
        log.trace("В сервис {} получен запрос на получение отзывов о фильме id {} в количестве {}",
                this.getClass(),
                filmId,
                count);
        if (!filmStorage.isFilmExists(filmId)) {
            throw new FilmIdUnknownException(String.valueOf(filmId));
        }
        return reviewStorage.getReviewByFilm(filmId, count);
    }

    public void addLike(long id, long userId) {
        log.trace("В сервис {} получен запрос на добавление like к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewStorage.isReviewExists(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!userStorage.isUserExists(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (!reviewStorage.isLikeExists(id, userId, true)) {
            reviewStorage.addLike(id, userId);
            Review review = reviewStorage.get(id);
            review.setUseful(review.getUseful() + 1);
            reviewStorage.updateUseful(review);
        } else {
            throw new LikeAlreadyExistsException(id, userId);
        }
    }

    public void addDislike(long id, long userId) {
        log.trace("В сервис {} получен запрос на добавление dislike к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewStorage.isReviewExists(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!userStorage.isUserExists(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (!reviewStorage.isLikeExists(id, userId, false)) {
            reviewStorage.addDislike(id, userId);
            Review review = reviewStorage.get(id);
            review.setUseful(review.getUseful() - 1);
            reviewStorage.updateUseful(review);
        } else {
            throw new DislikeAlreadyExistsException(id, userId);
        }
    }

    public void deleteLike(long id, long userId) {
        log.trace("В сервис {} получен запрос на удаление like к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewStorage.isReviewExists(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!userStorage.isUserExists(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (reviewStorage.isLikeExists(id, userId, true)) {
            reviewStorage.deleteLike(id, userId);
            Review review = reviewStorage.get(id);
            review.setUseful(review.getUseful() - 1);
            reviewStorage.updateUseful(review);
        } else {
            throw new LikeNotExistsException(id, userId);
        }
    }

    public void deleteDislike(long id, long userId) {
        log.trace("В сервис {} получен запрос на удаление dislike к отзыву с id {} от пользователя {}",
                this.getClass(),
                id,
                userId);
        if (!reviewStorage.isReviewExists(id)) {
            throw new ReviewNotExistsException(id);
        }
        if (!userStorage.isUserExists(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (reviewStorage.isLikeExists(id, userId, false)) {
            reviewStorage.deleteDislike(id, userId);
            Review review = reviewStorage.get(id);
            review.setUseful(review.getUseful() + 1);
            reviewStorage.updateUseful(review);
        } else {
            throw new DislikeNotExistsException(id, userId);
        }
    }
}
