package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Получение списка пользователей
     * @return Список пользователей
     */
    @Override
    public List<User> findAll() {
        final String findAllUsers = "SELECT * " +
                "FROM users";
        return jdbcTemplate.query(findAllUsers, this::mapRowToUser);
    }

    /**
     * Добавление пользователя
     * @param user Добавляемый пользователь
     * @return Добавленный пользователь
     */
    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());

        return user;
    }

    /**
     *  Обновление данных пользователя
     * @param user Обновляемый пользователь
     * @return Обновлённый пользователь
     */
    @Override
    public User update(User user) {
        final String updateUser = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(updateUser
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        return getUser(user.getId());
    }

    /**
     * Удаление пользователя
     * @param user Удаляемый пользователь
     * @return Статус (true - false)
     */
    @Override
    public boolean delete(User user) {
        String deleteUser = "DELETE FROM users " +
                "WHERE USER_ID = ?";
        return jdbcTemplate.update(deleteUser, user.getId()) > 0;
    }

    /**
     * Получение пользователя по ID
     * @param id Id пользователя
     * @return Пользователь
     */
    @Override
    public User getUser(long id) {
        final String findUserById = "SELECT * " +
                "FROM users " +
                "WHERE USER_ID = ?";
        return jdbcTemplate.queryForObject(findUserById, this::mapRowToUser, id);
    }

    /**
     * Получение списка друзей пользователя
     * @param id Id пользователя
     * @return Список друзей
     */
    @Override
    public List<User> getFriendList(long id) {
        final String findUserFriendsById = "SELECT u.* " +
                "FROM USER_FRIENDS us " +
                "LEFT JOIN users u ON us.FRIEND_ID = u.USER_ID " +
                "WHERE us.USER_ID = ? ";
        return jdbcTemplate.query(findUserFriendsById, this::mapRowToUser, id);
    }

    /**
     * Добавление друга
     * @param id Id пользователя, которому добавляется друг
     * @param friendId Id друга
     * @return Статус (true - false)
     */
    @Override
    public boolean addFriend(long id, long friendId) {
        String checkFriendship = "SELECT CONFIRMED_BY_FRIEND " +
                "FROM USER_FRIENDS " +
                "WHERE USER_ID = ? " +
                "AND USER_ID = ?";

        SqlRowSet statusRowsUser1 = jdbcTemplate.queryForRowSet(checkFriendship, id, friendId);
        SqlRowSet statusRowsUser2 = jdbcTemplate.queryForRowSet(checkFriendship, friendId, id);
        if (statusRowsUser1.toString().equals("Confirmed")) {
            return false;
        } else if (statusRowsUser1.toString().equals("Not Confirmed")) {
            return false;
        } else if (statusRowsUser2.toString().equals("Confirmed")) {
            return false;
        } else if (statusRowsUser2.toString().equals("Not Confirmed")) {
            final String updateFriendship = "UPDATE USER_FRIENDS SET " +
                    "CONFIRMED_BY_FRIEND = ? " +
                    "WHERE USER_ID = ?";
            jdbcTemplate.update(updateFriendship, "Confirmed", id);
            return true;
        } else {
            String insertFriendship = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID, CONFIRMED_BY_FRIEND) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(insertFriendship, id, friendId, "Not Confirmed");
            return true;
        }
    }

    /**
     * Удаление пользователя из друзей
     * @param id Id пользователя
     * @param friendId Id удаляемого друга
     * @return Статус (true - false)
     */
    @Override
    public boolean deleteFriend(long id, long friendId) {
        String deleteFriend = "DELETE FROM USER_FRIENDS " +
                "WHERE USER_ID = ? " +
                "AND FRIEND_ID = ?";
        return jdbcTemplate.update(deleteFriend, id, friendId) > 0;
    }

    /**
     * Получение списка id друзей пользователя
     * @param id Пользователь
     * @return Set Id друзей пользователя
     */
    @Override
    public Set<Long> getFriendsIdListByUserId(long id) {
        final String findFriendsIdByUserId = "SELECT FRIEND_ID " +
                "FROM USER_FRIENDS " +
                "WHERE USER_ID = ? ";
        return new HashSet<>(jdbcTemplate.query(findFriendsIdByUserId, (rs, friend_id) -> rs.getLong("friend_id"), id));
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
