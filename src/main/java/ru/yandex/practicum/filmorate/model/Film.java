package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.*;

@Data
@Builder
public class Film {
    private long id;
    private Set<Long> likes;
    private List<Genre> genres;
    @NotNull
    @NotBlank
    private Rating rating;
    @NotNull
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date releaseDate;
    @NotNull
    @Positive
    private int duration;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("rating_id", rating.getId());
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        return values;
    }
}