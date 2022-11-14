package ru.yandex.practicum.filmorate.exeptions;

public class FilmSearchInvalidParamsException extends RuntimeException {
    public FilmSearchInvalidParamsException(final String searchParams) {
        super(String.format("Неверные параметры поиска фильма: %s . Ожидалось: title ; director ; title,director", searchParams));
    }
}