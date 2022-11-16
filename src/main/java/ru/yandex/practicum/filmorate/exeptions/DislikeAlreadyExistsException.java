package ru.yandex.practicum.filmorate.exeptions;

public class DislikeAlreadyExistsException extends RuntimeException {
    public DislikeAlreadyExistsException(long id, long userId) {
        super(String.format("Пользователь с id %d уже добавлял dislike к этому фильму %d", id, userId));
    }
}
