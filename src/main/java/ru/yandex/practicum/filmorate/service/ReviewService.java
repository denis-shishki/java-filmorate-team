package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.EventType;
import ru.yandex.practicum.filmorate.constants.Operation;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewAlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeService likeService;
    private final UserService userService;

    public Review createReview(Review review) {
        log.info("Поступил запрос на создание отзыва с телом = {}", review);

        filmStorage.findFilm(review.getFilmId())
                .orElseThrow(() -> new FilmNotFoundException("Попытка оставить отзыв несуществующему фильму"));

        userStorage.findUser(review.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Попытка оставить отзыв несуществующим пользователем"));

        Review newReview = reviewStorage.create(review);
        userService.addEvent(newReview.getUserId(), EventType.REVIEW, Operation.ADD, newReview.getReviewId());
        return newReview;
    }

    public Review getReviewById(Integer id) {
        log.info("Поступил запрос на поиск отзыва по id = {}", id);

        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Запрашиваемый отзыв не существует"));

        log.info("Отзыв с id = {} успешно найден", id);
        return review;
    }

    public Optional<Review> updateReview(Review review) {
        log.info("Поступил запрос на обновление отзыва id = {}, с телом = {}", review.getReviewId(), review);

        Optional<Review> optionalReview = reviewStorage.update(review);
        if (optionalReview.isEmpty()) {
            throw new ReviewNotFoundException("Отзыва с таким идентификатором не найдено");
        }

        Review updateReview = optionalReview.get();
        userService.addEvent(updateReview.getUserId(), EventType.REVIEW, Operation.UPDATE, updateReview.getReviewId());

        return optionalReview;
    }

    public void deleteReview(Integer id) {
        log.info("Поступил запрос на удаление отзыва с id = {}", id);

        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Запрашиваемый отзыв не существует"));
        reviewStorage.delete(id);

        userService.addEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, review.getReviewId());
    }

    public List<Review> getAllReviews() {
        log.info("Поступил запрос на получение всех отзывов в приложении");

        return reviewStorage.getAll().stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public List<Review> getAllReviewsByFilm(Integer filmId, int count) {
        log.info("Поступил запрос на получения отзывов к фильму = {}, в количестве = {}", filmId, count);

        filmStorage.findFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Запрашиваемый фильм не существует"));

        List<Review> allReviewsByFilm;
        try {
            allReviewsByFilm = reviewStorage.findReviewsByFilm(filmId, count);

            log.info("Запрос на получение отзывов к фильму = {}, в количестве = {} успешно обработан", filmId, count);
        } catch (RuntimeException e) {
            throw new RuntimeException("Произошла ошибка при поиске отзывов по id фильма");
        }

        return allReviewsByFilm.stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .limit(count).collect(Collectors.toList());
    }

    public void likeReview(Integer id, Integer userId) {

        userStorage.findUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Review review;
        try {
            likeService.likeReviewByUser(id, userId);

            review = reviewStorage.findById(id)
                    .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден"));
            review.addLikeToUseful();
            reviewStorage.updateLikeOrDislike(review);
            reviewStorage.update(review);

        } catch (DuplicateKeyException e) {
            throw new ReviewAlreadyLikedException("Ошибка при добавлении лайка отзыву");
        }
        log.info("Лайк к отзыву = {} успешно добавлен. Useful отзыва теперь = {}", id, review.getUseful());
    }

    public void disLikeReview(Integer id, Integer userId) {
        userStorage.findUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Review review;
        try {
            likeService.dislikeReviewByUser(id, userId);


            review = reviewStorage.findById(id)
                    .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден"));
            review.addDislikeToUseful();
            reviewStorage.updateLikeOrDislike(review);
            reviewStorage.update(review);

        } catch (DuplicateKeyException e) {
            throw new ReviewAlreadyLikedException("Ошибка при добавлении дизлайка отзыву");
        }
        log.info("Дизлайк к отзыву = {} успешно добавлен. Useful отзыва теперь = {}", id, review.getUseful());
    }

    public void deleteLikeReview(Integer id, Integer userId) {

        userStorage.findUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Review review;
        try {
            likeService.deleteLikeReviewByUser(id, userId);

            review = reviewStorage.findById(id)
                    .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден"));
            review.addDislikeToUseful();
            reviewStorage.updateLikeOrDislike(review);
            reviewStorage.update(review);

        } catch (DuplicateKeyException e) {
            throw new ReviewAlreadyLikedException("Ошибка при удалении лайка отзыву");
        }
        log.info("Лайк к отзыву = {} успешно удален. Useful отзыва теперь = {}", id, review.getUseful());
    }

    public void deleteDislikeReview(Integer id, Integer userId) {
        userStorage.findUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Review review;
        try {
            likeService.deleteDislikeReviewByUser(id, userId);

            review = reviewStorage.findById(id)
                    .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден"));
            review.addLikeToUseful();
            reviewStorage.updateLikeOrDislike(review);
            reviewStorage.update(review);

        } catch (DuplicateKeyException e) {
            throw new ReviewAlreadyLikedException("Ошибка при удалении дизлайка отзыву");
        }
        log.info("Дизлайк к отзыву = {} успешно удален. Useful отзыва теперь = {}", id, review.getUseful());
    }
}
