package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exeptions.FilmIdUnknownException;
import ru.yandex.practicum.filmorate.exeptions.MethodNotImplementedException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private int filmId = 1;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {

        if (films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() + " уже существует");
            throw new FilmAlreadyExistException("Фильм с id: " + film.getId() + " уже существует");
        }

        film.setId(generateFilmId());
        log.info("Post \"/films\" " + film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() + " не найден");
            throw new FilmIdUnknownException("Фильм с id: " + film.getId() + " не найден");
        }

        log.info("Put \"/films\" " + film);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public boolean delete(long id) {
        throw new MethodNotImplementedException("Метод \"InMemoryFilmStorage.delete\" ещё не реализован");
    }

    public Film getFilm(long id) {
        if (!films.containsKey(id)) {
            throw new FilmIdUnknownException("Фильм с id: " + id + " не найден");
        }
        log.info("Get \"/films\" " + id);
        return films.get(id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        throw new MethodNotImplementedException("Метод \"InMemoryFilmStorage.getPopularFilms\" не реализован");
    }

    @Override
    public List<Film> getPopularFilms(int count, long genreId) {
        throw new MethodNotImplementedException("Метод \"InMemoryFilmStorage.getPopularFilms\" не реализован");
    }

    @Override
    public List<Film> getPopularFilms(int count, int year) {
        throw new MethodNotImplementedException("Метод \"InMemoryFilmStorage.getPopularFilms\" не реализован");
    }

    @Override
    public List<Film> getPopularFilms(int count, long genreId, int year) {
        throw new MethodNotImplementedException("Метод \"InMemoryFilmStorage.getPopularFilms\" не реализован");
    }

    @Override
    public boolean addLike(long userId, long filmId) {
        throw new MethodNotImplementedException("Метод \"InMemoryFilmStorage.addLike\" не реализован");
    }

    @Override
    public boolean deleteLike(long userId, long filmId) {
        throw new MethodNotImplementedException("Метод \"InMemoryFilmStorage.deleteLike\" не реализован");
    }

    private int generateFilmId() {
        return filmId++;
    }
}
