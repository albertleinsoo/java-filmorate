package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private int userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {

        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {

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

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {

        if (!users.containsKey(user.getId())) {
            log.error("Нельзя обновить пользователя с неизвестным id: " + user.getId());
            throw new UserIdUnknownException("Нельзя обновить пользователя с неизвестным id: " + user.getId());
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
