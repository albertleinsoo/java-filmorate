package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll(){
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    /**
     *
     * @param id Id пользователя, который добавляет в друзья
     * @param friendId Id пользователя, которого добавляют в друзья
     */
    public void addFriend(final long id, final long friendId) {
        if (isUserExists(id) && isUserExists(friendId)) {
            //Добавление друга пользователю с id
            userStorage.getUser(id).getFriends().add(friendId);
            //Добавление друга пользователю с friendId
            userStorage.getUser(friendId).getFriends().add(id);
        }
    }

    /**
     *
     * @param id Id пользователя, который удаляет в друга
     * @param friendId Id пользователя, которого удаляют из друзей
     */
    public void deleteFriend(final long id, final long friendId) {
        if (isUserExists(id) && isUserExists(friendId)) {
            //Удаление друга пользователю с id
            userStorage.getUser(id).getFriends().remove(friendId);
            //Удаление друга пользователю с friendId
            userStorage.getUser(friendId).getFriends().remove(id);
        }
    }

    /**
     *
     * @param id Id пользователя
     * @return Список друзей пользователя
     */
    public List<User> userFriends(final long id) {
        //Формируется список друзей пользователя
        List<User> friends = new ArrayList<>();
        for (Long friendId: userStorage.getUser(id).getFriends()) {
            friends.add(userStorage.getUser(friendId));
        }

        return friends;
    }

    public List<User> commonFriends(final long id, final long otherId) {
        List<User> commonFriends = new ArrayList<>();
        for (Long friendId: userStorage.getUser(id).getFriends()) {
            if (userStorage.getUser(otherId).getFriends().contains(friendId)) {
                commonFriends.add(userStorage.getUser(friendId));
            }
        }

        return commonFriends;
    }

    private boolean isUserExists(final long id) {
        userStorage.getUser(id);
        return true;
    }
}