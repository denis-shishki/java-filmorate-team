package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review createReview(Review review) {
        log.info("Поступил запрос на создание отзыва с телом = {}", review);

        filmStorage.findFilm(review.getFilmId())
                .orElseThrow(() -> new FilmNotFoundException("Попытка оставить отзыв несуществующему фильму"));

        userStorage.findUser(review.getId())
                .orElseThrow(() -> new UserNotFoundException("Попытка оставить отзыв несуществующим пользователем"));
        return reviewStorage.create(review);
    }

    public Review getReviewById(Integer id) {
        log.info("Поступил запрос на поиск отзыва по id = {}", id);

        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Запрашиваемый отзыв не существует"));

        log.info("Отзыв с id = {} успешно найден", id);
        return review;
    }

    public Review updateReview(Review review) {
        log.info("Поступил запрос на обновление отзыва id = {}, с телом = {}", review.getId(), review);

        return reviewStorage.update(review);
    }
}
