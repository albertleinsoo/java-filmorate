package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review get(long id);

    Review update(Review review);

    Review updateUseful(Review review);

    boolean delete(long id);

    List<Review> getAll();

    List<Review> getReviewByFilm(long filmId, int count);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    void deleteDislike(long id, long userId);

    boolean checkReview(Review review);

    boolean checkReview(long id);

    boolean checkLike(long reviewId, long userId, boolean liked);
}
