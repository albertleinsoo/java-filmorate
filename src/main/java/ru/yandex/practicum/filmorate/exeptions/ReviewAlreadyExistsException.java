package ru.yandex.practicum.filmorate.exeptions;

import ru.yandex.practicum.filmorate.model.Review;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(Review review) {
        super(String.format("Отзыв пользователя с id %d н фильм с id %d уже существует",
                review.getUserId(),
                review.getFilmId()));
    }
}
