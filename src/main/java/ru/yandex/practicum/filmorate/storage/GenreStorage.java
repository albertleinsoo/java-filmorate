package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * Интерфейс хранения жанров фильмов
 */
@Component
public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenreById(long id);
}
