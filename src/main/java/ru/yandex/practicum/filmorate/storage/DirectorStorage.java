package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

/**
 * Интерфейс хранения режжисеров
 */
public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(long directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(long directorId);
}
