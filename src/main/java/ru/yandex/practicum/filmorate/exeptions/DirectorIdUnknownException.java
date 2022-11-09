package ru.yandex.practicum.filmorate.exeptions;

public class DirectorIdUnknownException extends RuntimeException {
    public DirectorIdUnknownException(final long directorId) {
        super("Режжисер не найден, id: " + directorId);
    }
}
