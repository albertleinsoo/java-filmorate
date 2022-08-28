package ru.yandex.practicum.filmorate.exeptions;

public class UserIdUnknownException extends RuntimeException {
    public UserIdUnknownException(final String message) {
        super(message);
    }
}
