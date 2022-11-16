package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final RecommendationService recommendationService;

    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> userFriends(@PathVariable long id) {
        return userService.userFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.commonFriends(id, otherId);
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{userId}/feed")
    public List<Event> getFeed(@PathVariable long userId) {
        return eventService.getFeed(userId);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable long id) {
        return recommendationService.getRecommendedFilms(id);
    }
}