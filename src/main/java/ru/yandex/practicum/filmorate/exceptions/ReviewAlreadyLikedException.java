package ru.yandex.practicum.filmorate.exceptions;

public class ReviewAlreadyLikedException extends RuntimeException {
    public ReviewAlreadyLikedException(String message) {
        super(message);
    }
}
