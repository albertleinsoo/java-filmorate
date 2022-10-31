package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * Интерфейс хранения жанров фильмов
 */
public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenreById(long id);
}
