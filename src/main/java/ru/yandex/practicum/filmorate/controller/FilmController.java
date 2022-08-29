package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    private int filmId = 1;

    @GetMapping("/films")
    public List<Film> findAll() {

        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {

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

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {

        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() +" не найден");
            throw new FilmIdUnknownException("Фильм с id: " + film.getId() +" не найден");
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
