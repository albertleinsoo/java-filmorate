package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/popular")
    public List<Film> popular (@RequestParam @PathVariable Optional<Integer> count){
        if (count.isPresent()) {
            throw new MethodNotImplementedException("FilmController.popular");
        } else {
            throw new MethodNotImplementedException("FilmController.popular");
        }
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable long id) {
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
    public Film addLike (@PathVariable long id, @PathVariable long userId) {
        throw new MethodNotImplementedException("FilmController.addLike");
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLike(@PathVariable long id, @PathVariable long userId)
    {
        throw new MethodNotImplementedException("FilmController.removeLike");
    }

}