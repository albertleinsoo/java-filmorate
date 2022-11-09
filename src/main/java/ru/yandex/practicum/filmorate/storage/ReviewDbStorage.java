package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Primary
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        return review;
    }

    @Override
    public Review get(long id) {
        final String SQL_GET_REVIEW_BY_ID = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL " +
                "FROM REVIEWS WHERE REVIEW_ID = ?";
        return jdbcTemplate.queryForObject(SQL_GET_REVIEW_BY_ID, this::mapRowToReview, id);
    }

    @Override
    public Review update(Review review) {
        final String SQL_UPDATE_REVIEW = "UPDATE REVIEWS SET " +
                "CONTENT = ?, " +
                "IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(SQL_UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return review;
    }

    @Override
    public Review updateUseful(Review review) {
        final String SQL_UPDATE_REVIEW = "UPDATE REVIEWS SET " +
                "USEFUL = ? " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(SQL_UPDATE_REVIEW, review.getUseful(), review.getReviewId());
        return review;
    }

    @Override
    public boolean delete(long id) {
        final String SQL_DELETE_REVIEW = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        return jdbcTemplate.update(SQL_DELETE_REVIEW, id) > 0;
    }

    @Override
    public List<Review> getAll() {
        final String SQL_GET_ALL_REVIEWS = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL " +
                "FROM REVIEWS " +
                "ORDER BY USEFUL DESC ";
        return jdbcTemplate.query(SQL_GET_ALL_REVIEWS, this::mapRowToReview);
    }

    @Override
    public List<Review> getReviewByFilm(long filmId, int count) {
        final String SQL_GET_REVIEW_BY_FILM = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL " +
                "FROM REVIEWS " +
                "WHERE FILM_ID = ? " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(SQL_GET_REVIEW_BY_FILM, this::mapRowToReview, filmId, count);
    }

    @Override
    public void addLike(long id, long userId) {
        final String SQL_ADD_LIKE = "INSERT INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, LIKED) VALUES (?, ?, ?)";
        jdbcTemplate.update(SQL_ADD_LIKE, id, userId, true);
    }

    @Override
    public void addDislike(long id, long userId) {
        final String SQL_ADD_DISLIKE = "INSERT INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, LIKED) VALUES (?, ?, ?)";
        jdbcTemplate.update(SQL_ADD_DISLIKE, id, userId, false);
    }

    @Override
    public void deleteLike(long id, long userId) {
        final String SQL_DELETE_LIKE = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND LIKED = ?";
        jdbcTemplate.update(SQL_DELETE_LIKE, id, userId, true);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        final String SQL_DELETE_DISLIKE = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND LIKED = ?";
        jdbcTemplate.update(SQL_DELETE_DISLIKE, id, userId, false);
    }

    @Override
    public boolean isReviewExists(Review review) {
        final String SQL_CHECK_REVIEW = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM REVIEWS WHERE USER_ID = ? AND FILM_ID = ?";
        String result = jdbcTemplate.query(SQL_CHECK_REVIEW,
                (rs, rn) -> rs.getString("result"),
                review.getUserId(), review.getFilmId()).get(0);
        return Boolean.parseBoolean(result);
    }

    @Override
    public boolean isReviewExists(long id) {
        final String SQL_CHECK_REVIEW = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM REVIEWS WHERE REVIEW_ID = ?";
        String result = jdbcTemplate.query(SQL_CHECK_REVIEW,
                (rs, rn) -> rs.getString("result"),
                id).get(0);
        return Boolean.parseBoolean(result);
    }

    @Override
    public boolean isLikeExists(long reviewId, long userId, boolean liked) {
        final String SQL_CHECK_LIKE = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND LIKED = ?";
        String result = jdbcTemplate.query(SQL_CHECK_LIKE,
                (rs, rn) -> rs.getString("result"),
                reviewId, userId, liked).get(0);
        return Boolean.parseBoolean(result);
    }


    @Override
    public boolean isUserExists(long userId) {
        final String SQL_CHECK_USER = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM USERS WHERE USER_ID = ?";
        String result = jdbcTemplate.query(SQL_CHECK_USER, (rs, rn) -> rs.getString("result"), userId).get(0);
        return Boolean.parseBoolean(result);
    }

    @Override
    public boolean isFilmExists(long filmId) {
        final String SQL_CHECK_FILM = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM FILMS WHERE FILM_ID = ?";
        String result = jdbcTemplate.query(SQL_CHECK_FILM, (rs, rn) -> rs.getString("result"), filmId).get(0);
        return Boolean.parseBoolean(result);
    }

    private Review mapRowToReview(ResultSet resultSet, int i) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("REVIEW_ID"))
                .content(resultSet.getString("CONTENT"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .useful(resultSet.getInt("USEFUL"))
                .build();
    }
}
