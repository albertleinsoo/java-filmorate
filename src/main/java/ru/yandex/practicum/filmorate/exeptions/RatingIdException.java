package ru.yandex.practicum.filmorate.exeptions;

public class RatingIdException extends RuntimeException {
    public RatingIdException(final String message) {
        super(message);
    }
}