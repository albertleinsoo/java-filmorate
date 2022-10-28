package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController (GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Возвращает список всех жанров
     * @return Список жанров
     */
    @GetMapping()
    public List<Genre> findAll() {
        log.debug("Текущее количество пользователей: {}", genreService.getGenreAll().size());

        return genreService.getGenreAll();
    }

    /**
     * Возвращает жанр по id
     *
     * @param id Id жанра
     * @return Жанр
     */
    @GetMapping("/{id}")
    public Genre getUserById(@Valid @PathVariable int id) {
        return genreService.getGenreById(id);
    }
}
