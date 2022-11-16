package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;


@Builder
@Getter
@Setter
public class Director {
    private long id;
    @NotBlank
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        return values;
    }
}
