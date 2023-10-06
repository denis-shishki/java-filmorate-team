package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withSchemaName("public")
                .withTableName("review")
                .usingColumns("content", "is_positive", "user_id", "film_id", "useful")
                .usingGeneratedKeyColumns("review_id");
        insert.compile();

        int id = (int) insert.executeAndReturnKey(Map.of(
                "content", review.getContent(),
                "is_positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "useful", review.getUseful()
        ));
        review.setId(id);

        Review saveReview = findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Произошла ошибка при сохранении отзыва"));
        log.info("Отзыв с id = {} успешно сохранен", review.getId());
        return saveReview;
    }

    @Override
    public Optional<Review> findById(int id) {
        String sql = "SELECT * FROM review WHERE review_id = ?;";
        Review review;

        ReviewMapper reviewMapper = new ReviewMapper();
        review = jdbcTemplate.queryForObject(sql, reviewMapper, id);

        if (review == null) {
            return Optional.empty();
        }

        return Optional.of(review);
    }

    @Override
    public Review update(Review review) {
        final int reviewId = review.getId();
        final String sql = "UPDATE review " +
                "SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ?" +
                "WHERE review_id = ?;";

        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                reviewId
        );

        log.info("Отзыв = {} успешно обновлен", review.getId());
        return review;
    }

    @Override
    public void delete(Integer id) {
        final String sql = "DELETE FROM review WHERE review_id = ?;";
        jdbcTemplate.update(sql, id);

        log.info("Запрос на удаление отзыва по id успешно отработан");
    }


    @RequiredArgsConstructor
    private class ReviewMapper implements RowMapper<Review> {

        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {

            return Review.builder()
                    .id(rs.getInt("review_id"))
                    .content(rs.getString("content"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .userId(rs.getInt("user_id"))
                    .filmId(rs.getInt("film_id"))
                    .useful(rs.getInt("useful"))
                    .build();
        }
    }
}
