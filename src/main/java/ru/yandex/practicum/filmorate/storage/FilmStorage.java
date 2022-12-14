package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

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

    List<Film> getCommonFilms(long userId, long friendId);

    boolean addLike(long id, long userID);

    boolean deleteLike(long userId, long filmId);

    boolean isFilmExists(long filmId);

    List<Film> getDirectorFilmsSortedBy(long directorId, String sortBy);

    List<Film> searchFilmsByTitleDirector(String query, Set<String> by);

    List<Long[]> getAllLikes();

    List<Film> getFilmsByIdList(List<Long> recommendedFilmsId);
}
