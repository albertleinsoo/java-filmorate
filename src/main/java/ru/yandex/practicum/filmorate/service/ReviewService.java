package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.DislikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.DislikeNotExistsException;
import ru.yandex.practicum.filmorate.exeptions.FilmIdUnknownException;
import ru.yandex.practicum.filmorate.exeptions.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.LikeNotExistsException;
import ru.yandex.practicum.filmorate.exeptions.NullUserOrFilmIdException;
import ru.yandex.practicum.filmorate.exeptions.ReviewAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.ReviewNotExistsException;
import ru.yandex.practicum.filmorate.exeptions.UserIdUnknownException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;
    private final EventService eventService;

    public Review create(Review review) {
        log.trace("В сервис {} получен запрос на создание отзыва {}", this.getClass(), review.toString());
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new NullUserOrFilmIdException();
        }
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

        var createdReview = reviewDbStorage.create(review);
        eventService.createAddReviewEvent(createdReview.getUserId(), createdReview.getReviewId());
        return createdReview;
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
        if (!reviewDbStorage.checkReview(review.getReviewId())) {
            throw new ReviewNotExistsException(review);
        }

        reviewDbStorage.update(review);

        var updatedReview = reviewDbStorage.get(review.getReviewId());
        eventService.createUpdateReviewEvent(updatedReview.getUserId(), updatedReview.getReviewId());
        return updatedReview;
    }

    public boolean delete(long id) {
        log.trace("В сервис {} получен запрос на удаление отзыва с id {}", this.getClass(), id);
        if (!reviewDbStorage.checkReview(id)) {
            throw new ReviewNotExistsException(id);
        }

        var deletedReview = reviewDbStorage.get(id);
        var isDeleted = reviewDbStorage.delete(id);
        if (isDeleted) {
            eventService.createRemoveReviewEvent(deletedReview.getUserId(), deletedReview.getReviewId());
        }
        return isDeleted;
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
            reviewDbStorage.updateUseful(review);
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
            reviewDbStorage.updateUseful(review);
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
            reviewDbStorage.updateUseful(review);
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
            reviewDbStorage.updateUseful(review);
        } else {
            throw new DislikeNotExistsException(id, userId);
        }
    }
}
