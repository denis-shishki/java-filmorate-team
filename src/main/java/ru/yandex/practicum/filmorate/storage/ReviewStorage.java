package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Optional<Review> findById(int id);

    Optional<Review> update(Review review);

    void delete(Integer id);

    List<Review> getAll();

    List<Review> findReviewsByFilm(Integer filmId, int count);

    void updateLikeOrDislike(Review review);
}
