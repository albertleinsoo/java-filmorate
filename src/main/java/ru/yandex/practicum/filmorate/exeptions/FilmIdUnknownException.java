package ru.yandex.practicum.filmorate.exeptions;

public class FilmIdUnknownException extends RuntimeException {
    public FilmIdUnknownException(final long filmId) {
        super("Фильм с id: " + filmId +" не найден");
    }
}
