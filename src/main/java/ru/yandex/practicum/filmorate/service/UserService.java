package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.UserIdUnknownException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (validateUser(user)) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            return userStorage.create(user);
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    /**
     * Добавление пользователя в список друзей
     *
     * @param id       Id пользователя, который добавляет в друзья
     * @param friendId Id пользователя, которого добавляют в друзья
     */
    public void addFriend(final long id, final long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new UserIdUnknownException(id);
        }
        if (userStorage.getUser(friendId) == null) {
            throw new UserIdUnknownException(friendId);
        }

        userStorage.addFriend(id, friendId);
    }

    /**
     * Удаление пользователя из списка друзей
     *
     * @param id       Id пользователя, который удаляет в друга
     * @param friendId Id пользователя, которого удаляют из друзей
     */
    public void deleteFriend(final long id, final long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new UserIdUnknownException(id);
        }
        if (userStorage.getUser(friendId) == null) {
            throw new UserIdUnknownException(friendId);
        }

        userStorage.deleteFriend(id, friendId);
    }

    /**
     * Друзья пользователя
     *
     * @param id Id пользователя
     * @return Список друзей пользователя
     */
    public List<User> userFriends(final long id) {
        //Формируется список друзей пользователя
        return userStorage.getFriendList(id);
    }

    /**
     * Список общих друзей с пользователем
     *
     * @param id      id пользователя
     * @param otherId id другого пользователя, с которым нужно найти общих друзей
     * @return Список пользователей, которые являются общими друзьями пользователей id и otherId
     */
    public List<User> commonFriends(final long id, final long otherId) {
        if (userStorage.getUser(id) == null) {
            throw new UserIdUnknownException(id);
        }
        if (userStorage.getUser(otherId) == null) {
            throw new UserIdUnknownException(otherId);
        }

        Set<Long> userFriends = new HashSet<>(userStorage.getFriendsIdListByUserId(id));
        Set<Long> otherFriends = new HashSet<>(userStorage.getFriendsIdListByUserId(otherId));

        userFriends.retainAll(otherFriends);

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userId -> userStorage.getUser(userId))
                .collect(Collectors.toList());
    }

    private boolean validateUser(User user) {
        try {
            if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
                throw new ValidationException("Email должен быть заполнен и содержать символ '@'");
            } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
                throw new ValidationException("Необходимо указать login. Logon не должен содержать пробелов");
            } else if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            } else {
                return true;
            }
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            return false;
        }
    }
}