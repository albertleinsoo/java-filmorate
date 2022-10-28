package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Rating {
    private long id;
    private String name;

    public Rating() {}
    public Rating(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
