package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final DirectorDbStorage directorDbStorage;

    /**
     * @return Список всех фильмов
     */
    @Override
    public List<Film> findAll() {
        final String findAllFilms = "SELECT FILMS.*, RATINGS.RATING_NAME " +
                "FROM FILMS, RATINGS " +
                "WHERE FILMS.RATING_ID = RATINGS.RATING_ID";
        return jdbcTemplate.query(findAllFilms, this::mapRowToFilm);
    }

    /**
     * Добавление фильма
     *
     * @param film Добавляемый фильм
     * @return Добавленный фильм с обновлённым списком жанров
     */
    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());

        film.setDirectors(updateDirectors(film).getDirectors());
        film.setGenres(updateGenres(film).getGenres());
        return film;
    }

    /**
     * Обновление фильма
     *
     * @param film Обновляемый фильм
     * @return Фильм с обновлённым списком жанров и режжисеров
     */
    @Override
    public Film update(Film film) {

        final String updateFilm = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(updateFilm
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());


        final String DELETE_GENRES_BY_FILM_ID = "DELETE FROM film_genre " +
                "WHERE film_id = ?";
        final String DELETE_DIRECTORS_BY_FILM_ID = "DELETE FROM film_director " +
                "WHERE film_id = ?";


        jdbcTemplate.update(DELETE_GENRES_BY_FILM_ID, film.getId());
        jdbcTemplate.update(DELETE_DIRECTORS_BY_FILM_ID, film.getId());

        film.setDirectors(updateDirectors(film).getDirectors());
        film.setGenres(updateGenres(film).getGenres());


        return film;
    }

    /**
     * Удаление фильма
     *
     * @param id Id удаляемого фильма
     * @return Статус операции (true или false)
     */
    @Override
    public boolean delete(long id) {
        String deleteFilm = "DELETE FROM films " +
                "WHERE film_id = ?";
        return (jdbcTemplate.update(deleteFilm, id)) > 0;
    }

    /**
     * Получение фильма по id
     *
     * @param id Id того фильма, который нужно получить
     * @return Фильм
     */
    @Override
    public Film getFilm(long id) {
        final String findFilmById = "SELECT FILMS.*, RATINGS.RATING_NAME " +
                "FROM films, RATINGS " +
                "WHERE FILMS.RATING_ID = RATINGS.RATING_ID AND film_id = ?;";
        Film film = jdbcTemplate.queryForObject(findFilmById, this::mapRowToFilm, id);
        if (film.getGenres().size() == 0) {
            film.setGenres(null);

        }

        return film;
    }

    /**
     * Получение списка популярных фильмов
     *
     * @param count количество получаемых фильмов
     * @return Список фильмов
     */
    @Override
    public List<Film> getPopularFilms(int count) {
        final String findPopularFilmsWithLikes = "SELECT f.*, R.RATING_NAME " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON fl.film_id = f.film_id " +
                "LEFT JOIN RATINGS R on f.RATING_ID = R.RATING_ID " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(findPopularFilmsWithLikes, this::mapRowToFilm, count);
    }

    /**
     * Добавление лайка фильму
     *
     * @param userId пользователь
     * @param filmId фильм
     * @return Статус операции (true или false)
     */
    @Override
    public boolean addLike(long filmId, long userId) {
        String insertFilmLike = "INSERT INTO FILM_LIKES (film_id, user_id) " +
                "VALUES(?, ?)";
        return (jdbcTemplate.update(insertFilmLike, filmId, userId)) > 0;
    }

    /**
     * Удаление лайка фильма
     *
     * @param userId пользователь
     * @param filmId фильм
     * @return Статус операции (true или false)
     */
    @Override
    public boolean deleteLike(long userId, long filmId) {
        String deleteFilmLike = "DELETE FROM FILM_LIKES " +
                "WHERE film_id = ? " +
                "AND user_id = ?";
        return (jdbcTemplate.update(deleteFilmLike, filmId, userId)) > 0;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .mpa(new Rating(resultSet.getLong("rating_id"), resultSet.getString("rating_name")))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .genres(getSetGenres(resultSet))
                .directors(getSetDirectors(resultSet))
                .build();
    }

    private Rating getSetRating(ResultSet rs) throws SQLException {
        final String findMpaById = "SELECT * " +
                "FROM RATINGS " +
                "WHERE RATING_ID = ?";

        Rating rating = new Rating();
        rating.setId(rs.getLong("rating_id"));

        rating.setName(jdbcTemplate.queryForObject(findMpaById, this::mapRowToRating, rating.getId()).getName());
        return rating;
    }

    private Film updateGenres(Film film) {
        if (film.getGenres() != null) {
            if (film.getGenres().size() == 0) {
                return film;
            }
            String insertFilmGenre = "INSERT INTO film_genre (film_id, genre_id) " +
                    "VALUES(?, ?)";
            film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .forEach(id -> jdbcTemplate.update(insertFilmGenre, film.getId(), id));
        }
        return getFilm(film.getId());
    }

    private Film updateDirectors(Film film) {
        if (film.getDirectors() != null) {
            if (film.getDirectors().size() == 0) {
                return film;
            }
            String insertFilmDirector = "INSERT INTO film_director (film_id, director_id) VALUES(?, ?);";
            film.getDirectors().stream()
                    .map(Director::getId)
                    .distinct()
                    .forEach(id -> jdbcTemplate.update(insertFilmDirector, film.getId(), id));
        }
        return getFilm(film.getId());
    }

    private List<Genre> getSetGenres(ResultSet rs) throws SQLException {
        final String findGenreByFilmId = "SELECT fg.genre_id, " +
                "g.name " +
                "FROM film_genre fg " +
                "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";

        long filmId = rs.getLong("film_id");
        return jdbcTemplate.query(findGenreByFilmId, this::mapRowToGenre, filmId);
    }

    private List<Director> getSetDirectors(ResultSet rs) throws SQLException {
        final String findDirectorByFilmId = "SELECT fd.director_id, " +
                "d.name " +
                "FROM film_director fd " +
                "LEFT JOIN director d ON fd.director_id = d.director_id " +
                "WHERE fd.film_id = ?";

        long filmId = rs.getLong("film_id");
        return jdbcTemplate.query(findDirectorByFilmId, this::mapRowToDirector, filmId);
    }

    public List<Film> getDirectorFilmsSortedBy(long directorId, String sortBy) {
        directorDbStorage.checkDirector(directorId);

        final String findPopularFilmsWithLikes = "SELECT f.*, R.RATING_NAME " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON fl.film_id = f.film_id " +
                "LEFT JOIN RATINGS R on f.RATING_ID = R.RATING_ID " +
                "LEFT JOIN FILM_DIRECTOR fd on fd.film_id = f.film_id " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC;";

        final String findPopularFilmsWithYear = "SELECT f.*, R.RATING_NAME " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON fl.film_id = f.film_id " +
                "LEFT JOIN RATINGS R on f.RATING_ID = R.RATING_ID " +
                "LEFT JOIN FILM_DIRECTOR fd on fd.film_id = f.film_id " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY f.RELEASE_DATE;";

        return jdbcTemplate.query(sortBy.equals("year") ? findPopularFilmsWithYear : findPopularFilmsWithLikes, this::mapRowToFilm, directorId);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}