package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Optional<Review> findById(int id);

    Review update(Review review);
}
