package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("ratingDbStorage")
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> getAllRatings() {
        final String FIND_ALL_MPA = "SELECT * " +
                "FROM RATINGS";
        return jdbcTemplate.query(FIND_ALL_MPA, this::mapRowToRating);
    }

    @Override
    public Rating getRatingById(long ratingId) {
        final String FIND_MPA_BY_ID = "SELECT * " +
                "FROM RATINGS " +
                "WHERE RATING_ID = ?";

        return jdbcTemplate.queryForObject(FIND_MPA_BY_ID, this::mapRowToRating, ratingId);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }
}
