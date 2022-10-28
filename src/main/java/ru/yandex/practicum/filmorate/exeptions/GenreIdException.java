package ru.yandex.practicum.filmorate.exeptions;

public class GenreIdException extends RuntimeException {
    public GenreIdException(final String message) {
        super(message);
    }
}