package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * Интерфейс хранения фильмов
 */
public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    boolean delete(long id);

    Film getFilm(long id);

    List<Film> getPopularFilms(int count);

    List<Film> getPopularFilms(int count, long genreId);

    List<Film> getPopularFilms(int count, int year);

    List<Film> getPopularFilms(int count, long genreId, int year);

    List<Film> getCommonFilms(long userId, long friendId);

    boolean addLike(long id, long userID);

    boolean deleteLike(long userId, long filmId);

}
