package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private long eventId;

    private long timestamp;

    private long userId;

    private EventOperation operation;

    private EventType eventType;

    private long entityId;

}
