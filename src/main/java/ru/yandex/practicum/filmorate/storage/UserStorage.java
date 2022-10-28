package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс хранения пользователей
 */
@Component
public interface UserStorage {
    List<User> findAll();
    User create(User film);
    User update(User film);
    User delete(long id);

    User getUser(long id);
}
