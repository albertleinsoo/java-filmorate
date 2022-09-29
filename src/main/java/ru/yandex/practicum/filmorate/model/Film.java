package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Film {
    private long id;
    private Set<Long> likes = new HashSet<>();
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
}