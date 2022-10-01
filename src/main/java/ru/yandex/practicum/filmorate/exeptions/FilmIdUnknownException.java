package ru.yandex.practicum.filmorate.exeptions;

public class FilmIdUnknownException extends RuntimeException {
    public FilmIdUnknownException(final String message) {
        super(message);
    }
}
