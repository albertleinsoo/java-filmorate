package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    /**
     * Возвращает список всех режжисеров
     *
     * @return Список режжисеров
     */
    public List<Director> getDirectorAll() {
        log.debug("Получен список режжисеров");
        return directorStorage.getAllDirectors();
    }

    /**
     * Возвращает режжисера по ID
     *
     * @param directorId Id нужного режжисера
     * @return Режжисер
     */
    public Director getDirectorById(long directorId) {
        directorStorage.checkDirector(directorId);
        log.debug("Получен режжисер с id: " + directorId);
        return directorStorage.getDirectorById(directorId);
    }

    public Director createDirector(Director director) {
        log.debug("Cоздан режжисер: " + director.getName());
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        directorStorage.checkDirector(director.getId());
        log.debug("Обновлен режжисер с id: " + director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(long directorId) {
        directorStorage.checkDirector(directorId);
        log.debug("Удален режжисер с id: " + directorId);
        directorStorage.deleteDirectorById(directorId);
    }
}
