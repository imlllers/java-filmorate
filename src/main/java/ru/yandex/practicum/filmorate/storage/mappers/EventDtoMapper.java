package ru.yandex.practicum.filmorate.storage.mappers;

import ru.yandex.practicum.filmorate.dto.EventResponseDto;
import ru.yandex.practicum.filmorate.model.Event;

public class EventDtoMapper {

    public static EventResponseDto toDto(Event event) {
        return new EventResponseDto(
                event.getEventId(),
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );
    }
}