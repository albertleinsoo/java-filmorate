package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

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
    public List<Film> popular (@RequestParam(defaultValue = "10") final int count){
        return filmService.getPopular(count);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable final long id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getDirectorFilmsSortedBy(@PathVariable final long directorId, @RequestParam(defaultValue = "year") String sortBy ) {
        return filmService.getDirectorFilmsSortedBy(directorId,sortBy);
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
    public boolean addLike (@PathVariable final long id, @PathVariable final long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean removeLike(@PathVariable final long id, @PathVariable final long userId) {
        return filmService.deleteLike(id, userId);
    }

}