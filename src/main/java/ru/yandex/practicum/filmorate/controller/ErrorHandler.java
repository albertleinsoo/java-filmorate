package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeptions.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFilmAlreadyExistException(final FilmAlreadyExistException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFilmDateException(final FilmDateException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFilmIdUnknownException(final FilmIdUnknownException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUserAlreadyExistException(final UserAlreadyExistException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserIdUnknownException(final UserIdUnknownException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserIdUnknownException(final EmptyResultDataAccessException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleRatingIdUnknownException(final RatingIdException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleGenreIdUnknownException(final GenreIdException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleThrowable(final Throwable e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUserLoginAlreadyExistsException(final UserLoginAlreadyExistsException e) {
        return e.getMessage();
    }

    @ExceptionHandler({DislikeNotExistsException.class,
            ReviewNotExistsException.class,
            LikeNotExistsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundExceptions(final RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({DislikeAlreadyExistsException.class,
            ReviewAlreadyExistsException.class,
            LikeAlreadyExistsException.class,
            NullUserOrFilmIdException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAlreadyExistsExceptions(final RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(FailedToCreateEventException e) {
        var message = "Failed to create event";
        log.error("{}: {}", message, e.getEvent());
        return getExceptionResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> getExceptionResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }

}
