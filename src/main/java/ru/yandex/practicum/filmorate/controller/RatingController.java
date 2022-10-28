package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rating")
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Возвращает список всех рейтонгов
     * @return Список рейтингов
     */
    @GetMapping()
    public List<Rating> findAll() {
        log.debug("Текущее количество пользователей: {}", ratingService.getRatingAll().size());

        return ratingService.getRatingAll();
    }

    /**
     * Возвращает рейтинг по id
     *
     * @param id объекта mpa
     * @return объект mpa
     */
    @GetMapping("/{id}")
    public Rating getRatingById(@Valid @PathVariable int id) {
        log.debug("Поиск mpa: {}", ratingService.getRatingById(id).getName());

        return ratingService.getRatingById(id);
    }
}
