package ru.yandex.practicum.filmorate.exeptions;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(final String message) {
        super(message);
    }
}
