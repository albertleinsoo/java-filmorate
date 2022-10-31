package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.RatingIdException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;

@Slf4j
@Service
public class RatingService {
    private final RatingStorage ratingStorage;

    public RatingService(@Qualifier("ratingDbStorage") RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    /**
     * Возвращает список всех рейтингов фильмов
     * @return Список рейтингов
     */
    public List<Rating> getRatingAll() {
        return ratingStorage.getAllRatings();
    }

    /**
     * Возвращает рейтинг по id
     * @param ratingId Id рейтинга
     * @return Возвращаемый рейтинг
     */
    public Rating getRatingById(int ratingId) {
        if (ratingId <= 0) {
            throw new RatingIdException("id рейтинга должен быть больше 0");
        }
        return ratingStorage.getRatingById(ratingId);
    }
}
