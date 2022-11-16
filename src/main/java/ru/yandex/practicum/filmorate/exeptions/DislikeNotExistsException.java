package ru.yandex.practicum.filmorate.exeptions;

public class DislikeNotExistsException extends RuntimeException {
    public DislikeNotExistsException(long id, long userId) {
        super(String.format("Пользователь с id %d не добавлял dislike к этому фильму %d", id, userId));
    }
}
