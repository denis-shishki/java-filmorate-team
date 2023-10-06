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

    private int id;
    @NotNull(message = "Описание не может быть пустым")
    private String content;
    @NotNull(message = "Отзыв должен быть положительным или отрицательным")
    private Boolean isPositive;
    @NotNull(message = "id пользователя не может быть пустым")
    @Positive(message = "id пользователя не может быть отрицательным")
    private Integer userId;
    @NotNull(message = "id фильма не может быть пустым")
    @Positive(message = "id фильма не может быть отрицательным")
    private Integer filmId;
    private int useful;


    public void addLikeToUseful() {
        useful = useful + 1;
    }
}
