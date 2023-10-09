package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeDbStorage likeDbStorage;

    public void likeFilm(Integer filmId, Integer userId) {
        likeDbStorage.likeFilm(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likeDbStorage.deleteLike(filmId, userId);
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