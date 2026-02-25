package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    @Override
    public void addEvent(Event event) {
        String sql = """
                INSERT INTO events (user_id, event_type, operation, entity_id, timestamp)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                event.getTimestamp()
        );
    }

    @Override
    public List<Event> findByUserId(Integer userId) {
        String sql = """
                SELECT event_id, user_id, event_type, operation, entity_id, timestamp
                FROM events
                WHERE user_id = ?
                ORDER BY event_id ASC
                """;

        return jdbcTemplate.query(sql, eventRowMapper, userId);
    }
}
