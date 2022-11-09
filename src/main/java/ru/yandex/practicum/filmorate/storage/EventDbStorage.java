package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("eventDbStorage")
@RequiredArgsConstructor
@Slf4j
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean createEvent(Event event) {
        log.debug("Create event: {}", event);

        var sqlQuery = "INSERT INTO events (user_id, operation, event_type, entity_id) " +
                "VALUES (?, ?, ?, ?)";

        var sqlParams = new Object[]{
                event.getUserId(),
                event.getOperation().name(),
                event.getEventType().name(),
                event.getEntityId()
        };

        int rowsAffected = jdbcTemplate.update(sqlQuery, sqlParams);
        return rowsAffected == 1;
    }

    @Override
    public List<Event> readEvents(long userId) {
        log.debug("Read feed: userId = {}", userId);

        var sqlQuery = "SELECT e.event_id " +
                "   , CAST(EXTRACT(EPOCH FROM e.time_stamp) * 1000 AS BIGINT) AS time_stamp " +
                "   , e.user_id " +
                "   , e.operation " +
                "   , e.event_type " +
                "   , e.entity_id " +
                "FROM events e " +
                "WHERE e.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapEvent, userId);
    }

    private Event mapEvent(ResultSet rs, int i) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("time_stamp"))
                .userId(rs.getLong("user_id"))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }

}
