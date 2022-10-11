package ru.yandex.practicum.filmorate.exeptions;

public class UserIdUnknownException extends RuntimeException {
    public UserIdUnknownException(final long userId) {
        super("Пользователь не найден, id: " + userId);
    }
}
