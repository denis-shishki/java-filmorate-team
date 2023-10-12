package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.EventType;
import ru.yandex.practicum.filmorate.constants.Operation;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeDbStorage likeDbStorage;
    private final UserService userService;
    private final FilmService filmService;


    public void likeFilm(Integer filmId, Integer userId) {
        userService.findUser(userId);
        filmService.findFilm(filmId);

        likeDbStorage.likeFilm(filmId, userId);
        userService.addEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        userService.findUser(userId);
        filmService.findFilm(filmId);
        likeDbStorage.deleteLike(filmId, userId);
        userService.addEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    public void likeReviewByUser(Integer id, Integer userId) {
        log.info("Получен запрос для добавления лайка отзыву = {} от пользователя = {}", id, userId);

        likeDbStorage.likeReview(id, userId);
    }

    public void dislikeReviewByUser(Integer id, Integer userId) {
        log.info("Получен запрос для добавления дизлайка отзыву = {} от пользователя = {}", id, userId);

        likeDbStorage.dislikeReview(id, userId);
    }

    public void deleteLikeReviewByUser(Integer id, Integer userId) {
        log.info("Получен запрос для удаления лайка отзыву = {} от пользователя = {}", id, userId);

        likeDbStorage.deleteLikeReview(id, userId);
    }

    public void deleteDislikeReviewByUser(Integer id, Integer userId) {
        log.info("Получен запрос для удаления дизлайка отзыву = {} от пользователя = {}", id, userId);

        likeDbStorage.deleteDislikeReview(id, userId);
    }
}