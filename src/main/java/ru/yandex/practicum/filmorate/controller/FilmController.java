package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/popular")
    public List<Film> getPopular(
            @RequestParam(defaultValue = "10", required = false) int count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/films/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable final long id) {
        return filmService.getFilm(id);
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public boolean addLike(@PathVariable final long id, @PathVariable final long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean removeLike(@PathVariable final long id, @PathVariable final long userId) {
        return filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/films/{filmId}")
    public void deleteFilm(@PathVariable final long filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getDirectorFilmsSortedBy(@PathVariable final long directorId, @RequestParam String sortBy ) {
        return filmService.getDirectorFilmsSortedBy(directorId,sortBy);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilmsByTitleDirector(@RequestParam String query, @RequestParam String by) {
        String[] byParams = by.split(",");
        Set<String> searchParams = new HashSet<>(Arrays.asList(byParams));
        return filmService.searchFilmsByTitleDirector(query, searchParams);
    }
}