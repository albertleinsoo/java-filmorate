package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User create(User film) {
        return null;
    }

    @Override
    public User update(User film) {
        return null;
    }

    @Override
    public User delete(long id) {
        return null;
    }

    @Override
    public User getUser(long id) {
        return null;
    }
}
