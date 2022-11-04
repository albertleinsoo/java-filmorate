package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.GenreIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("genreDbStorage") GenreStorage genresStorage) {
        this.genreStorage = genresStorage;
    }

    /**
     * Возвращает список всех жанров
     * @return Список жанров
     */
    public List<Genre> getGenreAll() {
        return genreStorage.getAllGenres();
    }

    /**
     * Возвращает жанр по ID
     * @param genreId Id нужного жанра
     * @return Жанр
     */
    public Genre getGenreById(int genreId) {
        if (genreId <= 0) {
            throw new GenreIdException("id жанра должен быть больше 0");
        }
        return genreStorage.getGenreById(genreId);
    }
}
