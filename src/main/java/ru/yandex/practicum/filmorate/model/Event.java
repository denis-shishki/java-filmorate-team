package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.constants.EventType;
import ru.yandex.practicum.filmorate.constants.Operation;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Event {
    private long timestamp;
    private int userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private Operation operation;
    private int eventId;
    private int entityId;
}
