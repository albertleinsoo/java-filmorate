package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exeptions.FailedToCreateEventException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    @Qualifier("eventDbStorage")
    private final EventStorage eventStorage;

    public List<Event> getFeed(long userId) {
        log.debug("Get feed: userId = {}", userId);

        return eventStorage.readEvents(userId);
    }

    public void createAddFriendEvent(long userId, long friendId) {
        log.debug("Create 'add friend' event: userId = {}, friendId = {}", userId, friendId);

        var event = buildFriendEvent(userId, EventOperation.ADD, friendId);
        create(event);
    }

    public void createRemoveFriendEvent(long userId, long friendId) {
        log.debug("Create 'delete friend' event: userId = {}, friendId = {}", userId, friendId);

        var event = buildFriendEvent(userId, EventOperation.REMOVE, friendId);
        create(event);
    }

    public void createAddLikeEvent(long userId, long filmId) {
        log.debug("Create 'add like' event: userId = {}, filmId = {}", userId, filmId);

        var event = buildLikeEvent(userId, EventOperation.ADD, filmId);
        create(event);
    }

    public void createRemoveLikeEvent(long userId, long filmId) {
        log.debug("Create 'delete like' event: userId = {}, filmId = {}", userId, filmId);

        var event = buildLikeEvent(userId, EventOperation.REMOVE, filmId);
        create(event);
    }

    public void createAddReviewEvent(long userId, long reviewId) {
        log.debug("Create 'add review' event: userId = {}, reviewId = {}", userId, reviewId);

        var event = buildReviewEvent(userId, EventOperation.ADD, reviewId);
        create(event);
    }

    public void createUpdateReviewEvent(long userId, long reviewId) {
        log.debug("Create 'update review' event: userId = {}, reviewId = {}", userId, reviewId);

        var event = buildReviewEvent(userId, EventOperation.UPDATE, reviewId);
        create(event);
    }

    public void createRemoveReviewEvent(long userId, long reviewId) {
        log.debug("Create 'remove review' event: userId = {}, reviewId = {}", userId, reviewId);

        var event = buildReviewEvent(userId, EventOperation.REMOVE, reviewId);
        create(event);
    }

    private void create(Event event) {
        log.debug("Create event: {}", event);

        var isCreated = eventStorage.createEvent(event);
        if (!isCreated) {
            throw new FailedToCreateEventException(event);
        }
    }

    private Event buildFriendEvent(long userId, EventOperation eventOperation, long friendId) {
        return buildEvent(userId, eventOperation, EventType.FRIEND, friendId);
    }

    private Event buildLikeEvent(long userId, EventOperation eventOperation, long filmId) {
        return buildEvent(userId, eventOperation, EventType.LIKE, filmId);
    }

    private Event buildReviewEvent(long userId, EventOperation eventOperation, long reviewId) {
        return buildEvent(userId, eventOperation, EventType.REVIEW, reviewId);
    }

    private Event buildEvent(long userId, EventOperation eventOperation, EventType eventType, long entityId) {
        return Event.builder()
                .userId(userId)
                .operation(eventOperation)
                .eventType(eventType)
                .entityId(entityId)
                .build();
    }

}
