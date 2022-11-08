package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.FilmIdUnknownException;
import ru.yandex.practicum.filmorate.exeptions.UserIdUnknownException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private FilmStorage filmStorage;
    private FilmDbStorage filmDbStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
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

    public List<Film> getPopular(final int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getDirectorFilmsSortedBy(long directorId, String sortBy) {
        return filmDbStorage.getDirectorFilmsSortedBy(directorId, sortBy);
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
