package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

@Builder
@Getter
public class Event {

    private long eventId;

    private long timestamp;

    private long userId;

    private EventOperation operation;

    private EventType eventType;

    private long entityId;

}
