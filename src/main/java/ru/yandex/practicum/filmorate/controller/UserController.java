package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

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
        throw new MethodNotImplementedException("UserController.userFriends");
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable long id, @PathVariable long otherId) {
        throw new MethodNotImplementedException("UserController.commonFriends");
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
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        throw new MethodNotImplementedException("UserController.addFriend");
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable long id, @PathVariable String friendId) {
        throw new MethodNotImplementedException("UserController.removeFriend");
    }
}