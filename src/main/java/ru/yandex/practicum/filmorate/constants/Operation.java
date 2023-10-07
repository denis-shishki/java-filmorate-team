package ru.yandex.practicum.filmorate.constants;

public enum Operation {
    REMOVE(1),
    ADD(2),
    UPDATE(3);

    private final int value;

    Operation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Operation fromValue(int value) {
        for (Operation type : Operation.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
