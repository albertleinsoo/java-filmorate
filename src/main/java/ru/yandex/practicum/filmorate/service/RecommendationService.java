package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.UserIdUnknownException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class RecommendationService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public RecommendationService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getRecommendedFilms(Long userId) {
        //film_id, user_id
        if (!filmStorage.checkUserExist(userId)){
            throw new UserIdUnknownException(userId);
        }

        List<Long[]> allLikes = filmStorage.getAllLikes();

        if (allLikes.isEmpty()) {
            log.info("No users likes are currently present");
            return new ArrayList<>();
        }

        return cookRecommendationList(filmStorage.getAllLikes(), userId);
    }

    private List<Film> cookRecommendationList(List<Long[]> allLikes, Long userId) {
        List<Long> userLikes = getUserFavourites(allLikes, userId);
        if (userLikes.isEmpty()) {
            log.info("User hasn`t liked anything yet");
            return new ArrayList<>();
        }

        List<Long[]> allNoUserLikes = getNotUserLikes(allLikes, userId);
        List<Long> likesCrossingsUsers = getLikesCrossingsUsers(userLikes, allNoUserLikes);
        if (likesCrossingsUsers.isEmpty()) {
            log.info("No common favourites there`re for the user");
            return new ArrayList<>();
        }

        return cookRelevantFilmsList(likesCrossingsUsers, userLikes, allLikes);
    }

    private List<Long> getUserFavourites(List<Long[]> allLikes, Long userId) {
        //film_id, user_id
        List<Long> userLikes = new ArrayList<>();
        for (Long[] like : allLikes) {
            if (like[1] == userId) {
                userLikes.add(like[0]);
            }
        }
        return userLikes;
    }

    private List<Long[]> getNotUserLikes(List<Long[]> allLikes, Long userId) {
        List<Long[]> notUserLikes = new ArrayList<>();

        for (Long[] like : allLikes) {
            if (like[1] != userId) {
                notUserLikes.add(like);
            }
        }
        return notUserLikes;

    }

    private List<Long> getLikesCrossingsUsers(List<Long> userLikes, List<Long[]> notUserLikes) {
        Map<Long, Long> crossingCounts = new HashMap<>();
        //film_id, user_id
        for (Long[] like : notUserLikes) {
            if (userLikes.contains(like[0])) {
                if (crossingCounts.containsKey(like[1])) {
                    crossingCounts.put(like[1], (crossingCounts.get(like[1]) + 1));
                } else {
                    crossingCounts.put(like[1], 1L);
                }
            }
        }
        List<Long> sortedByCommonLikes = crossingCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(entry -> entry.getKey())
                .collect(toList());

        log.info("List of likes crossings prepared for the user");
        return sortedByCommonLikes;
    }

    private List<Film> cookRelevantFilmsList(List<Long> commonLikesUsers, List<Long> userLikes, List<Long[]> allLikes) {
        List<Film> recommended = new ArrayList<>();
        for (Long id : commonLikesUsers) {
            List<Long> userFavourites = getUserFavourites(allLikes, id);
            for (Long filmId : userFavourites) {
                if (!userLikes.contains(filmId) && !recommended.contains(filmId)) {
                    recommended.add(filmStorage.getFilm(filmId));
                }
            }
        }

        if (recommended.isEmpty()) {
            log.info("No recommendations there are for the user");
        }

        log.info("Recommendations list sent");
        return recommended;
    }
}

/*System.out.println("Список всех лайков: ");
        for (Long[] like : allLikes){
            System.out.println(Arrays.toString(like));
        }*/