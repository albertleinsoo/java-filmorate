package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.DirectorIdUnknownException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("directorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {
        final String findAllDirector = "SELECT * FROM DIRECTOR " +
                "ORDER BY director_id";
        return jdbcTemplate.query(findAllDirector, this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(long directorId) {
        final String findDirectorById = "SELECT * " +
                "FROM DIRECTOR " +
                "WHERE director_id = ?";
        return jdbcTemplate.queryForObject(findDirectorById, this::mapRowToDirector, directorId);
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        final String updateDirector = "UPDATE DIRECTOR SET name =? WHERE director_id = ?";
        jdbcTemplate.update(updateDirector, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirectorById(long directorId) {
        final String deleteDirectorById = "DELETE " +
                "FROM DIRECTOR " +
                "WHERE director_id = ?";
        jdbcTemplate.update(deleteDirectorById, directorId);

    }

    public void checkDirector(long directorId) {
        String sql = "SELECT COUNT(*) FROM DIRECTOR WHERE director_id = ?;";
        if (jdbcTemplate.queryForObject(sql, Integer.class, directorId) != 1) {
            log.warn("Режжисера с id " + directorId + " не существует");
            throw new DirectorIdUnknownException(directorId);
        }
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
