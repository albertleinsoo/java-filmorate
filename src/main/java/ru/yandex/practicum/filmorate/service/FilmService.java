package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilm(final long id) {
        return filmStorage.getFilm(id);
    }

    public Film addLike(final long id, final long userId) {
        if (isFilmExists(id) && userService.isUserExists(userId)) {
            filmStorage.getFilm(id).getLikes().add(userId);
        }
        return filmStorage.getFilm(id);
    }

    public Film deleteLike(final long id, final long userId) {
        if (isFilmExists(id) && userService.isUserExists(userId)) {
            filmStorage.getFilm(id).getLikes().remove(userId);
        }
        return filmStorage.getFilm(id);
    }

    public List<Film> getPopular(final int count) {
        List<Film> sortedFilms = filmStorage.findAll();

        sortedFilms.sort((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()));

        //Первые count элементов отсортированного по кол-ву лайков списка фильмов
        return sortedFilms.stream().limit(count).collect(Collectors.toList());
    }

    public boolean isFilmExists(final long id) {
        filmStorage.getFilm(id);
        return true;
    }
}
