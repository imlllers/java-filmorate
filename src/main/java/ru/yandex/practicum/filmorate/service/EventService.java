package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventResponseDto;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventStorage eventStorage;

    public void createEvent(Integer userId,
                            EventType eventType,
                            Operation operation,
                            Integer entityId) {

        Event event = new Event();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        event.setTimestamp(System.currentTimeMillis());

        eventStorage.addEvent(event);
    }

    public List<EventResponseDto> getUserFeed(Integer userId) {
        return eventStorage.findByUserId(userId)
                .stream()
                .map(EventDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
