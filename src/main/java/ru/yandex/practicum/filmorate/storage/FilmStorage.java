package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * Интерфейс хранения фильмов
 */
@Component
public interface FilmStorage {
    List<Film> findAll();
    Film create(Film film);
    Film update(Film film);
    boolean delete(long id);
    Film getFilm(long id);
    List<Film> getPopularFilms(int count);
    boolean addLike(long userId, long filmId);
    boolean deleteLike(long userId, long filmId);
}
