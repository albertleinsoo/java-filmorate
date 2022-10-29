package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.MethodNotImplementedException;
import ru.yandex.practicum.filmorate.exeptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exeptions.UserIdUnknownException;
import ru.yandex.practicum.filmorate.exeptions.UserLoginAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            log.error("Пользователь с таким id уже существует");
            throw new UserAlreadyExistException("Пользователь с таким id уже существует");
        }

        if (isContainsUserLogin(user.getLogin())) {
            log.error("Пользователь с логином: " + user.getLogin() + " уже существует");
            throw new UserLoginAlreadyExistsException("Пользователь с логином: " + user.getLogin() + " уже существует");
        }

        user.setId(generateUserId());
        // если имя пустое, оно заполняется логином
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        log.info("Post \"/users\" " + user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Нельзя обновить пользователя с неизвестным id: " + user.getId());
            throw new UserIdUnknownException(user.getId());
        }

        if (isContainsUserLogin(user.getLogin())) {
            log.error("Пользователь с логином: " + user.getLogin() + " уже существует");
            throw new UserLoginAlreadyExistsException("Пользователь с логином: " + user.getLogin() + " уже существует");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        log.info("Put \"/users\" " + user);
        users.put(user.getId(), user);
        return user;
    }



    @Override
    public User getUser(long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id: " + id + " не найден");
            throw new UserIdUnknownException(id);
        }
        return users.get(id);
    }

    @Override
    public boolean delete(User user) {
        throw new MethodNotImplementedException("Метод \"InMemoryUserStorage.delete\" ещё не реализован");
    }

    @Override
    public List<User> getFriendList(long id) {
        throw new MethodNotImplementedException("Метод \"InMemoryUserStorage.getFriendList\" ещё не реализован");
    }

    @Override
    public boolean addFriend(long id, long friendId) {
        throw new MethodNotImplementedException("Метод \"InMemoryUserStorage.addFriend\" ещё не реализован");
    }

    @Override
    public boolean deleteFriend(long id, long friendId) {
        throw new MethodNotImplementedException("Метод \"InMemoryUserStorage.deleteFriend\" ещё не реализован");
    }

    @Override
    public Set<Long> getFriendsIdListByUserId(long id) {
        throw new MethodNotImplementedException("Метод \"InMemoryUserStorage.getFriendsIdListByUserId\" ещё не реализован");
    }

    /**
     * Генерирует новый id для пользователя
     * @return сгенерированный id пользователя
     */
    private int generateUserId() {
        return userId++;
    }

    private boolean isContainsUserLogin(String login) {
        boolean isContainsLogin = false;
        for (User user: users.values()) {
            if (user.getLogin().equals(login)) {
                isContainsLogin = true;
                break;
            }
        }
        return isContainsLogin;
    }
}
