package ru.yandex.practicum.filmorate.exeptions;

public class NullUserOrFilmIdException extends RuntimeException {
    public NullUserOrFilmIdException() {
        super(String.format("Id пользователя или фильма не указаны."));
    }
}
