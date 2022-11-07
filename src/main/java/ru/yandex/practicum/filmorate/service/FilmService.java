package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.FilmIdUnknownException;
import ru.yandex.practicum.filmorate.exeptions.UserIdUnknownException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {

        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

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

    public boolean addLike(final long id, final long userId) {
        if (id <= 0) {
            throw new FilmIdUnknownException("Фильм с id: " + id + " не найден");
        }

        if (userId <= 0) {
            throw new UserIdUnknownException(userId);
        }
        return filmStorage.addLike(id, userId);
    }

    public boolean deleteLike(final long id, final long userId) {
        if (id <= 0) {
            throw new FilmIdUnknownException("Фильм с id: " + id + " не найден");
        }

        if (userId <= 0) {
            throw new UserIdUnknownException(userId);
        }

        return filmStorage.deleteLike(userId, id);
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

    public List<Film> getCommonFilms(long userId, long friendId) {

        if (userStorage.isUserExists(userId)) {
            throw new UserIdUnknownException(userId);
        } else if (userStorage.isUserExists(friendId)) {
            throw new UserIdUnknownException(friendId);
        }

        return filmStorage.getCommonFilms(userId, friendId);
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
