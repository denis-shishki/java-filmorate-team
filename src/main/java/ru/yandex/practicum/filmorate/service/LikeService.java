package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.EventType;
import ru.yandex.practicum.filmorate.constants.Operation;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeDbStorage likeDbStorage;
    private final UserService userService;

    public void likeFilm(Integer filmId, Integer userId) {
        likeDbStorage.likeFilm(filmId, userId);
        userService.addEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likeDbStorage.deleteLike(filmId, userId);
        userService.addEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }
}