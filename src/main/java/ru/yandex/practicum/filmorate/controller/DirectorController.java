package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    /**
     * Возвращает список всех режжисеров
     *
     * @return Список режжисеров
     */
    @GetMapping()
    public List<Director> findAll() {
        return directorService.getDirectorAll();
    }

    /**
     * Возвращает режжисера по id
     *
     * @param id Id режжисера
     * @return режжисер
     */
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @PathVariable long id){
        directorService.deleteDirector(id);
    }
}

