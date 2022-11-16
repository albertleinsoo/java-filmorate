package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс хранения пользователей
 */
public interface UserStorage {
    List<User> findAll();

    User create(User film);

    User update(User film);

    void deleteUser(long userId);

    User getUser(long id);

    List<User> getFriendList(long id);

    boolean addFriend(long id, long friendId);

    boolean deleteFriend(long id, long friendId);

    Set<Long> getFriendsIdListByUserId(long id);

    boolean isUserExists(long userId);
}
