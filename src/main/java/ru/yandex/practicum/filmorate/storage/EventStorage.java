package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    boolean createEvent(Event event);

    List<Event> readEvents(long userId);

}
