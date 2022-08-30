package ru.yandex.practicum.filmorate.exeptions;

public class UserLoginAlreadyExistsException extends RuntimeException {
    public UserLoginAlreadyExistsException(final String message) {
        super(message);
    }
}