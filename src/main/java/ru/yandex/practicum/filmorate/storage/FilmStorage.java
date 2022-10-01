package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {
    List<Film> findAll();
    Film create(Film film);
    Film update(Film film);
    Film delete(long id);
    Film getFilm(long id);
}
