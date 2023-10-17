package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        final String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        final String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);

    }

    @Override
    public void likeReview(Integer id, int userId) {
        final String sql = "INSERT INTO likes_review (review_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void dislikeReview(Integer id, Integer userId) {
        final String sql = "INSERT INTO likes_review (review_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteLikeReview(Integer id, Integer userId) {
        final String sql = "DELETE FROM likes_review WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteDislikeReview(Integer id, Integer userId) {
        final String sql = "DELETE FROM likes_review WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }
}
