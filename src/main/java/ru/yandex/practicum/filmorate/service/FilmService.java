package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private UserStorage userStorage;

    private FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    public Film addLike(final long id, final long userId) {
        if (isFilmExists(id) && isUserExists(userId)) {
            filmStorage.getFilm(id).getLikes().add(userId);
        }
        return filmStorage.getFilm(id);
    }

    public Film deleteLike(final long id, final long userId) {
        if (isFilmExists(id) && isUserExists(userId)) {
            filmStorage.getFilm(id).getLikes().remove(userId);
        }
        return filmStorage.getFilm(id);
    }

    public List<Film> getPopular(final int count) {
        List<Film> sortedFilms = filmStorage.findAll();

        sortedFilms.sort((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()));

        //Первые count элементов отсортированного по кол-ву лайков списка фильмов
        return sortedFilms.stream().limit(count).collect(Collectors.toList());
    }

    private boolean isFilmExists(final long id) {
        filmStorage.getFilm(id);
        return true;
    }

    private boolean isUserExists(final long id) {
        userStorage.getUser(id);
        return true;
    }

    private void validateFilmDate (Film film) {

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
