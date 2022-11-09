package ru.yandex.practicum.filmorate.exeptions;

import ru.yandex.practicum.filmorate.model.Event;

public class FailedToCreateEventException extends RuntimeException {

    private final Event event;

    public FailedToCreateEventException(Event event) {
        super();
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

}
