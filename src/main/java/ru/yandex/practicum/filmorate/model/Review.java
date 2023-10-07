package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Review {

    private Integer reviewId;
    @NotNull(message = "Описание не может быть пустым")
    private String content;
    @NotNull(message = "Отзыв должен быть положительным или отрицательным")
    private Boolean isPositive;
    @NotNull(message = "id пользователя не может быть пустым")
    private Integer userId;
    @NotNull(message = "id фильма не может быть пустым")
    private Integer filmId;
    private int useful;


    public void addLikeToUseful() {
        useful = useful + 1;
    }

    public void addDislikeToUseful() {
        useful = useful - 1;
    }
}
