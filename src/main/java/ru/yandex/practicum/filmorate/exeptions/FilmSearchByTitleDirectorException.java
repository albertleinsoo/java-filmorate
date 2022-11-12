package ru.yandex.practicum.filmorate.exeptions;

public class FilmSearchByTitleDirectorException extends RuntimeException {
    public FilmSearchByTitleDirectorException(final String message) {
        super(message);
    }
}