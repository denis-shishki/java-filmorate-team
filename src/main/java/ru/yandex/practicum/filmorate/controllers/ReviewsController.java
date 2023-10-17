package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewsController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    @PutMapping
    public Optional<Review> updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable("id") Integer id) {
        reviewService.deleteReview(id);
    }

    @GetMapping
    public List<Review> getTopRatedFilms(@RequestParam(required = false) Integer filmId,
                                         @RequestParam(required = false, defaultValue = "10") @Positive int count) {

        if (filmId == null) {
            return reviewService.getAllReviews();
        }

        return reviewService.getAllReviewsByFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLikeInReview(@PathVariable("id") Integer id,
                                @PathVariable("userId") Integer userId) {
        reviewService.likeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislikeInReview(@PathVariable("id") Integer id,
                                   @PathVariable("userId") Integer userId) {
        reviewService.disLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeInReview(@PathVariable("id") Integer id,
                                   @PathVariable("userId") Integer userId) {
        reviewService.deleteLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDislikeInReview(@PathVariable("id") Integer id,
                                      @PathVariable("userId") Integer userId) {
        reviewService.deleteDislikeReview(id, userId);
    }
}
