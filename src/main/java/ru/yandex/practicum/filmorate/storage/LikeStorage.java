package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {

    void likeFilm(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    void likeReview(Integer id, int id1);

    void dislikeReview(Integer id, Integer userId);

    void deleteLikeReview(Integer id, Integer userId);

    void deleteDislikeReview(Integer id, Integer userId);
}
