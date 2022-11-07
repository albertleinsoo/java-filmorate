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

    void deleteFilm(long filmId);

    Film getFilm(long id);

    List<Film> getPopularFilms(int count);

    List<Film> getPopularFilms(int count, long genreId);

    List<Film> getPopularFilms(int count, int year);

    List<Film> getPopularFilms(int count, long genreId, int year);

    boolean addLike(long id, long userID);

    boolean deleteLike(long userId, long filmId);

    boolean isFilmExists(long filmId);

}
