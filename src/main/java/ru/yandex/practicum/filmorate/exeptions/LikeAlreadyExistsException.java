package ru.yandex.practicum.filmorate.exeptions;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException(long id, long userId) {
        super(String.format("Пользователь с id %d уже добавлял like к этому фильму %d", id, userId));
    }
}
