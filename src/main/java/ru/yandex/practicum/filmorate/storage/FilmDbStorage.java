package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

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

        film.setDirectors(updateFilmDirectors(film).getDirectors());
        film.setGenres(updateGenres(film).getGenres());
        return film;
    }

    /**
     * Обновление фильма
     *
     * @param film Обновляемый фильм
     * @return Фильм с обновлённым списком жанров
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

        film.setDirectors(updateFilmDirectors(film).getDirectors());
        film.setGenres(updateGenres(film).getGenres());


        return film;
    }

    /**
     * Удаление фильма
     *
     * @param filmId Id удаляемого фильма
     */
    @Override
    public void deleteFilm(long filmId) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
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
                "WHERE FILMS.RATING_ID = RATINGS.RATING_ID AND film_id = ?";
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
        var sql = "SELECT f.film_id " +
                "   , f.name " +
                "   , f.description " +
                "   , f.release_date " +
                "   , f.duration " +
                "   , f.rating_id " +
                "   , r.rating_name " +
                "   , f.rate " +
                "FROM films f, " +
                "   ratings r " +
                "WHERE f.rating_id = r.rating_id " +
                "ORDER BY f.rate, f.film_id DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getPopularFilms(int count, long genreId) {
        var sql = "WITH film_ids_by_genre_id AS ( " +
                "   SELECT fg.film_id " +
                "   FROM film_genre fg " +
                "   WHERE fg.genre_id = " + genreId +
                "), " +
                "films_by_film_ids AS ( " +
                "   SELECT f.film_id " +
                "       , f.name " +
                "       , f.description " +
                "       , f.release_date " +
                "       , f.duration " +
                "       , f.rating_id " +
                "       , r.rating_name " +
                "       , f.rate " +
                "   FROM film_ids_by_genre_id fi, " +
                "       films f, " +
                "       ratings r " +
                "   WHERE fi.film_id = f.film_id " +
                "   AND f.rating_id = r.rating_id " +
                ") " +
                "SELECT ff.* " +
                "FROM films_by_film_ids ff " +
                "ORDER BY ff.rate DESC " +
                "LIMIT " + count;

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Film> getPopularFilms(int count, int year) {
        var sql = "WITH film_ids_by_year AS ( " +
                "   SELECT f.film_id " +
                "   FROM films f " +
                "   WHERE YEAR(f.release_date) = " + year +
                "), " +
                "films_by_film_ids AS ( " +
                "   SELECT f.film_id " +
                "       , f.name " +
                "       , f.description " +
                "       , f.release_date " +
                "       , f.duration " +
                "       , f.rating_id " +
                "       , r.rating_name " +
                "       , f.rate " +
                "   FROM film_ids_by_year fi, " +
                "       films f, " +
                "       ratings r " +
                "   WHERE fi.film_id = f.film_id " +
                "   AND f.rating_id = r.rating_id " +
                ") " +
                "SELECT * " +
                "FROM films_by_film_ids ff " +
                "ORDER BY ff.rate DESC " +
                "LIMIT " + count;

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Film> getPopularFilms(int count, long genreId, int year) {
        var sql = "WITH film_ids_by_genre_ids AS ( " +
                "   SELECT fg.film_id " +
                "   FROM film_genre fg " +
                "   WHERE fg.genre_id = " + genreId +
                "), " +
                "film_ids_by_year AS ( " +
                "   SELECT f.film_id " +
                "   FROM film_ids_by_genre_ids fi, " +
                "       films f " +
                "   WHERE fi.film_id = f.film_id " +
                "   AND YEAR(f.release_date) = " + year +
                "), " +
                "films_by_film_ids AS ( " +
                "   SELECT f.film_id " +
                "       , f.name " +
                "       , f.description " +
                "       , f.release_date " +
                "       , f.duration " +
                "       , f.rating_id " +
                "       , r.rating_name " +
                "       , f.rate " +
                "   FROM film_ids_by_year fi, " +
                "       films f, " +
                "       ratings r " +
                "   WHERE fi.film_id = f.film_id " +
                "   AND f.rating_id = r.rating_id " +
                ") " +
                "SELECT * " +
                "FROM films_by_film_ids ff " +
                "ORDER BY ff.rate DESC " +
                "LIMIT " + count;

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    /**
     * Получение списка общих с другом фильмов
     *
     * @param userId   идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return Список фильмов
     */
    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "SELECT f.*, r.rating_name FROM films f " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT JOIN ratings AS r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id IN (SELECT DISTINCT fl1.film_id " +
                "                   FROM film_likes AS fl1 " +
                "                   JOIN film_likes AS fl2 ON fl1.film_id = fl2.film_id " +
                "                   WHERE fl1.user_id = ? AND fl2.user_id = ?) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(DISTINCT fl.user_id) DESC";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
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


    @Override
    public boolean isFilmExists(long filmId) {
        String sql = "SELECT 1 FROM films WHERE film_id = ? LIMIT 1";
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, filmId));
    }

    @Override
    public List<Long[]> getAllLikes() {
        List<Long[]> allLikes = new ArrayList<>();

        jdbcTemplate.query("select * from film_likes", (ResultSet rs) -> {
            Long[] like = {rs.getLong("film_id"), rs.getLong("user_id")};
            allLikes.add(like);
            while (rs.next()) {
                like = new Long[]{rs.getLong("film_id"), rs.getLong("user_id")};
                allLikes.add(like);
            }
        });
        return allLikes;
    }

    @Override
    public List<Film> getFilmsByIdList(List<Long> filmIds) {
        final String inSql = filmIds.stream().map(Object::toString)
                .collect(Collectors.joining(","));
        final String sqlQuery = "select films.*, ratings.rating_name" +
                " from films, ratings " +
                "where films.rating_id = ratings.rating_id and film_id in (" + inSql + ")";

        List<Film> recommendedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        log.info("Recommendations list returned");
        return recommendedFilms;

    }

    @Override
    public List<Film> searchFilmsByTitleDirector(String query, Set<String> by) {
        String sqlParam = "%" + query + "%";
        String sql =
                "SELECT F.*, R.RATING_NAME FROM FILMS F " +
                        "LEFT JOIN FILM_DIRECTOR FD ON FD.FILM_ID = F.FILM_ID " +
                        "LEFT JOIN DIRECTOR D ON D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                        "LEFT JOIN RATINGS R ON F.RATING_ID = R.RATING_ID " +
                        "LEFT JOIN FILM_LIKES FL ON FL.film_id = F.film_id " +
                        "WHERE UPPER(CONCAT(" +
                        (by.contains("title") ? "F.NAME" : "NULL") + ", " +
                        (by.contains("director") ? "D.NAME" : "NULL") +
                        ")) LIKE UPPER(?)" +
                        "GROUP BY F.film_id " +
                        "ORDER BY COUNT(FL.user_id) DESC;";

        return jdbcTemplate.query(sql, this::mapRowToFilm, sqlParam);

    }

    public List<Film> getDirectorFilmsSortedBy(long directorId, String sortBy) {

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

            List<Genre> genres = film.getGenres().stream().distinct().collect(Collectors.toList());
            jdbcTemplate.batchUpdate(insertFilmGenre, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }

        return getFilm(film.getId());
    }

    /**
     * Добавление режиссёров в фильм
     *
     * @param film фильм
     * @return объект film с добавленными режиссёрами
     */
    private Film updateFilmDirectors(Film film) {
        if (film.getDirectors() != null) {
            if (film.getDirectors().size() == 0) {
                return film;
            }
            String insertFilmDirector = "INSERT INTO film_director (film_id, director_id) VALUES(?, ?);";
            List<Director> directors = film.getDirectors().stream().distinct().collect(Collectors.toList());
            jdbcTemplate.batchUpdate(insertFilmDirector, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, directors.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
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

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .mpa(new Rating(resultSet.getLong("rating_id"), resultSet.getString("rating_name")))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .genres(getSetGenres(resultSet))
                .rate(resultSet.getInt("rate"))
                .directors(getSetDirectors(resultSet))
                .build();
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