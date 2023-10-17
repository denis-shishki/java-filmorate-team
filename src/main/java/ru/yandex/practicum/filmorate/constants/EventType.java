package ru.yandex.practicum.filmorate.constants;

public enum EventType {
    LIKE(1),
    REVIEW(2),
    FRIEND(3);
    private final int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EventType fromValue(int value) {
        for (EventType type : EventType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
