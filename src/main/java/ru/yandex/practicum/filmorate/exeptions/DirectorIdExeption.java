package ru.yandex.practicum.filmorate.exeptions;

public class DirectorIdExeption extends RuntimeException{

    public DirectorIdExeption(String message){
        super(message);
    }
}
