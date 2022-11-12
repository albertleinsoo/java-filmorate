package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.FilmIdUnknownException;
import ru.yandex.practicum.filmorate.exeptions.FilmSearchByTitleDirectorException;
import ru.yandex.practicum.filmorate.exeptions.UserIdUnknownException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilmDate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilmDate(film);
        return filmStorage.update(film);
    }

    public Film getFilm(final long id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        if (!userStorage.isUserExists(userId)) {
            throw new UserIdUnknownException(userId);
        }
        if (!userStorage.isUserExists(friendId)) {
            throw new UserIdUnknownException(friendId);
        }
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilm(long filmId) {
        if (!filmStorage.isFilmExists(filmId)) {
            throw new FilmIdUnknownException("Фильм с id: " + filmId + " не найден");
        }
        filmStorage.deleteFilm(filmId);
    }

    public boolean addLike(final long filmId, final long userId) {
        if (filmId <= 0) {
            throw new FilmIdUnknownException("Фильм с id: " + filmId + " не найден");
        }

        if (userId <= 0) {
            throw new UserIdUnknownException(userId);
        }

        var isLikeAdded = filmStorage.addLike(filmId, userId);
        if (isLikeAdded) {
            eventService.createAddLikeEvent(userId, filmId);
        }
        return isLikeAdded;
    }

    public boolean deleteLike(final long filmId, final long userId) {
        if (filmId <= 0) {
            throw new FilmIdUnknownException("Фильм с id: " + filmId + " не найден");
        }

        if (userId <= 0) {
            throw new UserIdUnknownException(userId);
        }

        var isLikeDeleted = filmStorage.deleteLike(userId, filmId);
        if (isLikeDeleted) {
            eventService.createRemoveLikeEvent(userId, filmId);
        }
        return isLikeDeleted;
    }

    public List<Film> getPopular(int count, Long genreId, Integer year) {
        List<Film> films;

        if (genreId == null && year == null) {
            films = filmStorage.getPopularFilms(count);
        } else if (genreId != null && year != null) {
            films = filmStorage.getPopularFilms(count, genreId, year);
        } else if (genreId != null) {
            films = filmStorage.getPopularFilms(count, genreId);
        } else {
            films = filmStorage.getPopularFilms(count, year);
        }

        return films;
    }

    public List<Film> getDirectorFilmsSortedBy(long directorId, String sortBy) {
        directorStorage.checkDirector(directorId);
        return filmStorage.getDirectorFilmsSortedBy(directorId, sortBy);
    }

    public List<Film> searchFilmsByTitleDirector(String query, String by) {
        if (by.equals("title") || by.equals("director") || by.equals("title,director") || by.equals("director,title")) {
            return filmStorage.searchFilmsByTitleDirector(query, by);
        } else {
            throw new FilmSearchByTitleDirectorException("Неверные параметры поиска фильма: " + by + " . " +
                    "Ожидалось: title ; director ; title,director");
        }
    }

    private void validateFilmDate(Film film) {
        try {
            if (film.getName().isEmpty()) {
                throw new ValidationException("Нет названия фильма");
            } else if (film.getDescription().isEmpty() || film.getDescription().length() > 200) {
                throw new ValidationException("Описание превышает 200 символов");
            } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Дата релиза должна быть после 1895-12-28");
            } else if (film.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма не может быть отрицательной");
            }
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

}
