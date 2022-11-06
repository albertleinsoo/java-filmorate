package ru.yandex.practicum.filmorate.exeptions;

import ru.yandex.practicum.filmorate.model.Review;

public class ReviewNotExistsException extends RuntimeException {
    public ReviewNotExistsException(Review review) {
        super(String.format("Отзыв пользователя с id %d на фильм с id %d не существует",
                review.getUserId(),
                review.getFilmId()));
    }

    public ReviewNotExistsException(long id) {
        super(String.format("Отзыв с id %d не существует", id));
    }
}
