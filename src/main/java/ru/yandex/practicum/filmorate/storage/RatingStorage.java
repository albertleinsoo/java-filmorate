package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

/**
 * Интерфейс хранения рейтингов фильмов
 */
@Component
public interface RatingStorage {
    List<Rating> getAllRatings();

    Rating getRatingById (long ratingId);
}
