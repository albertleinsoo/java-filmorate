package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        final String findAllGenre = "SELECT * FROM GENRE";
        return jdbcTemplate.query(findAllGenre, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(long genreId) {
        final String findGenreById =    "SELECT * " +
                                        "FROM GENRE " +
                                        "WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(findGenreById, this::mapRowToGenre, genreId);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
