package ru.yandex.practicum.filmorate.exeptions;

public class MethodNotImplementedException extends RuntimeException {
    public MethodNotImplementedException(final String message) {
        super(message);
    }
}