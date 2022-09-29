package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exeptions.FilmDateException;
import ru.yandex.practicum.filmorate.exeptions.FilmIdUnknownException;
import ru.yandex.practicum.filmorate.exeptions.MethodNotImplementedException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 1;
    private final Map<Long, Film> films = new HashMap<>();

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

        validateFilmDate(film);

        film.setId(generateFilmId());
        log.info("Post \"/films\" " + film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() +" не найден");
            throw new FilmIdUnknownException(film.getId());
        }

        validateFilmDate(film);

        // Если фильма нет в базе, для него генерируется id
        if (!films.containsKey(film.getId())) {
            film.setId(generateFilmId());
        }
        log.info("Put \"/films\" " + film);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film delete(long id) {
        throw new MethodNotImplementedException("InMemoryFilmStorage.delete");
    }

    public Film getFilm(long id) {
        if (!films.containsKey(id)) {
            throw new FilmIdUnknownException(id);
        }
        log.info("Get \"/films\" " + id);
        return films.get(id);
    }

    private int generateFilmId() {
        return filmId++;
    }

    private void validateFilmDate (Film film) {
        Calendar cal = Calendar.getInstance();
        cal.set(1895, Calendar.DECEMBER, 28);
        if (film.getReleaseDate().before(cal.getTime())) {
            log.error("Дата релиза должна быть после 1895-12-28");
            throw new FilmDateException("Дата релиза должна быть после 1895-12-28");
        }
    }

}
