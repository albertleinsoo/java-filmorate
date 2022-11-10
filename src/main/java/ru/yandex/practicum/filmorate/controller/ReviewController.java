package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/{id}")
    public Review get(@PathVariable long id) {
        log.trace("В контроллер {} получен запрос на получение отзыва с id {}", this.getClass(), id);
        return reviewService.get(id);
    }


    @PostMapping("/reviews")
    public Review create(@RequestBody @Valid Review review) {
        log.trace("В контроллер {} получен запрос на создание отзыва {}", this.getClass(), review.toString());
        return reviewService.create(review);
    }

    @PutMapping("/reviews")
    public Review update(@RequestBody @Valid Review review) {
        log.trace("В контроллер {} получен запрос на обновление отзыва {}", this.getClass(), review.toString());
        return reviewService.update(review);
    }

    @DeleteMapping("/reviews/{id}")
    public void delete(@PathVariable long id) {
        log.trace("В контроллер {} получен запрос на удаление отзыва {}", this.getClass(), id);
        reviewService.delete(id);
    }

    @GetMapping("/reviews")
    public List<Review> getReviewsByFilm(@RequestParam(required = false) Optional<Long> filmId,
                                         @RequestParam(required = false, defaultValue = "10") int count) {
        if (filmId.isPresent()) {
            log.trace("В контроллер {} получен запрос на поиск всех отзывов по фильму с id {}", this.getClass(), filmId);
            return reviewService.getReviewsByFilm(filmId.get(), count);
        } else {
            return reviewService.getAll();
        }

    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.trace("В контроллер {} получен запрос на добавление like фильму с id {} от пользователя с id {}",
                this.getClass(),
                id,
                userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.trace("В контроллер {} получен запрос на добавление dislike фильму с id {} от пользователя с id {}",
                this.getClass(),
                id,
                userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.trace("В контроллер {} получен запрос на удаление like фильму с id {} от пользователя с id {}",
                this.getClass(),
                id,
                userId);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable long id, @PathVariable long userId) {
        log.trace("В контроллер {} получен запрос на удаление dislike фильму с id {} от пользователя с id {}",
                this.getClass(),
                id,
                userId);
        reviewService.deleteDislike(id, userId);
    }
}
