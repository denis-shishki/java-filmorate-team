package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Review {

    private int reviewId;
    @NotNull(message = "Описание не может быть пустым")
    private String content;
    @NotNull(message = "Отзыв должен быть положительным или отрицательным")
    private Boolean isPositive;
    @NotNull
    @Positive(message = "id пользователя не может быть отрицательным")
    private Integer userId;
    @NotNull
    @Positive(message = "id фильма не может быть отрицательным")
    private Integer filmId;
    private int useful;

}
