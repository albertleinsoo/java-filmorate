package ru.yandex.practicum.filmorate.exeptions;

public class FilmDateException  extends RuntimeException {
    public FilmDateException(final String message) {
        super(message);
    }
}