package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventResponseDto {
    private Long eventId;
    private Long timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer entityId;
}