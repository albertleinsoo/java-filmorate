package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RecommendationService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public RecommendationService (@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getRecommendedFilms(Long userId){
        //film_id, user_id
        return cookRecommendationList(filmStorage.getAllLikes(), userId);
    }

    private List <Film> cookRecommendationList (Map <Long, Long> allLikes, Long userId){

    }

    private Map <Long, Long> getUserFavourites (Map <Long, Long> allLikes, Long userId){
        Map <Long, Long> userLikes =new HashMap<>();
        for (Long k : allLikes.keySet()){
            if (allLikes.get(k) == userId){
                userLikes.put(, k)
            }
        }
    }

    private Map <Long, Long> getAllLikesCrossings (){

    }







}
