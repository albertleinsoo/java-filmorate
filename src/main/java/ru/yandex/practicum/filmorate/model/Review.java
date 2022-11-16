package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Builder
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Review {
    private long reviewId;
    @NotNull
    private String content;
    private boolean isPositive;
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
    private long useful;

    public boolean getIsPositive() {
        return this.isPositive;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("CONTENT", content);
        values.put("IS_POSITIVE", isPositive);
        values.put("USER_ID", userId);
        values.put("FILM_ID", filmId);
        values.put("USEFUL", useful);
        return values;
    }
}
