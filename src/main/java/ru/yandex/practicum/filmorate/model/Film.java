package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    private long id;
    private Set<Long> likes;
    private List<Genre> genres;
    private List<Director> directors;
    private int rate;
    private Rating mpa;
    @NotNull
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private int duration;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("rate", rate);
        values.put("rating_id", mpa.getId());
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        return values;
    }
}