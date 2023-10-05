package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        return null;
    }
}
