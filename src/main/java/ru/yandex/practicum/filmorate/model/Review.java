package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Review {
    @JsonProperty("reviewId")
    private Integer id;

    @NotBlank(message = "Содержимое отзыва не может быть пустым")
    @Size(max = 2000, message = "Максимальная длина отзыва 2000 символов")
    private String content;

    @NotNull(message = "Тип отзыва (положительный/негативный) должен быть указан")
    private Boolean isPositive;

    private int useful = 0;

    @NotNull(message = "Идентификатор фильма должен быть указан")
    private Integer filmId;

    @NotNull(message = "Идентификатор пользователя должен быть указан")
    private Integer userId;
}
