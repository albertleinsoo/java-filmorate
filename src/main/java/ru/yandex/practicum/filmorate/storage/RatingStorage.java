package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

/**
 * Интерфейс хранения рейтингов фильмов
 */
public interface RatingStorage {
    List<Rating> getAllRatings();

    Rating getRatingById (long ratingId);
}
