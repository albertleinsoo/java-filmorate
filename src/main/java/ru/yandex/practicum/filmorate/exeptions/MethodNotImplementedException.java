package ru.yandex.practicum.filmorate.exeptions;

public class MethodNotImplementedException extends RuntimeException {
    public MethodNotImplementedException(final String method) {
        super("Метод \"" + method + "\" ещё не реализован");
    }
}