package ru.yandex.practicum.filmorate.exeptions;

public class DirectorIdNotExistException extends RuntimeException {
    public DirectorIdNotExistException(final long directorId) {
        super("Режжисер не найден, id: " + directorId);
    }
}
