package ru.yandex.practicum.filmorate.exeptions;

public class LikeNotExistsException extends RuntimeException {
    public LikeNotExistsException(long id, long userId) {
        super(String.format("Пользователь с id %d не добавлял like к этому фильму %d", id, userId));
    }
}
