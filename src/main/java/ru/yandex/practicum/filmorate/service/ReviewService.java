package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewAlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeService likeService;

    public Review createReview(Review review) {
        log.info("Поступил запрос на создание отзыва с телом = {}", review);

        filmStorage.findFilm(review.getFilmId())
                .orElseThrow(() -> new FilmNotFoundException("Попытка оставить отзыв несуществующему фильму"));

        userStorage.findUser(review.getUserId())
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

    public void deleteReview(Integer id) {
        log.info("Поступил запрос на удаление отзыва с id = {}", id);

        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Запрашиваемый отзыв не существует"));
        reviewStorage.delete(id);
    }

    public List<Review> getAllReviews() {
        log.info("Поступил запрос на получение всех отзывов в приложении");

        return reviewStorage.getAll();
    }

    public List<Review> getAllReviewsByFilm(Integer filmId, int count) {
        log.info("Поступил запрос на получения отзывов к фильму = {}, в количестве = {}", filmId, count);

        Film film = filmStorage.findFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Запрашиваемый фильм не существует"));

        List<Review> allReviewsByFilm;
        try {
            allReviewsByFilm = reviewStorage.findReviewsByFilm(filmId, count);
            log.info("Запрос на получение отзывов к фильму = {}, в количестве = {} успешно обработан", filmId, count);
        } catch (RuntimeException e) {
            throw new RuntimeException("Произошла ошибка при поиске отзывов по id фильма");
        }

        return allReviewsByFilm;
    }

    public void likeReview(Integer id, Integer userId) {

        User user = userStorage.findUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Review review;
        try {
            likeService.likeReviewByUser(id, userId);

            review = reviewStorage.findById(id)
                    .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден"));
            review.addLikeToUseful();
            reviewStorage.update(review);

        } catch (DuplicateKeyException e) {
            throw new ReviewAlreadyLikedException("Ошибка при добавлении лайка отзыву, лайк уже существует");
        }
        log.info("Лайк к отзыву = {} успешно добавлен. Useful отзыва теперь = {}", id, review.getUseful());
    }

    public void disLikeReview(Integer id, Integer userId) {
        User user = userStorage.findUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Review review;
        try {
            likeService.dislikeReviewByUser(id, userId);

            review = reviewStorage.findById(id)
                    .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден"));
            review.addDislikeToUseful();
            reviewStorage.update(review);

        } catch (DuplicateKeyException e) {
            throw new ReviewAlreadyLikedException("Ошибка при добавлении дизлайка отзыву, лайк уже существует");
        }
        log.info("Дизлайк к отзыву = {} успешно добавлен. Useful отзыва теперь = {}", id, review.getUseful());
    }
}
