-- возвращаем список пользователей, являющихся его друзьями.
SELECT U.LOGIN, U.NAME
FROM (
    SELECT FRIEND_ID FROM USERS AS U
    LEFT OUTER JOIN USER_FRIENDS AS UF ON U.USER_ID=UF.USER_ID
    WHERE U.USER_ID = 2 --Id Пользователя, друзей которого ищем
) AS UF_IDS
JOIN USERS AS U ON UF_IDS.FRIEND_ID=U.USER_ID

-- список друзей, общих с другим пользователем.
SELECT U1_FRIENDS.LOGIN, U1_FRIENDS.NAME
FROM (
    SELECT U.USER_ID, U.LOGIN, U.NAME
    FROM (
            SELECT FRIEND_ID FROM USERS AS U
            LEFT OUTER JOIN USER_FRIENDS AS UF ON U.USER_ID=UF.USER_ID
            WHERE U.USER_ID = 2 --Id первого пользователя Пользователя, друзей которого ищем
    ) AS UF_IDS
    JOIN USERS AS U ON UF_IDS.FRIEND_ID=U.USER_ID
) AS U1_FRIENDS
JOIN (
    SELECT U.USER_ID
    FROM (
            SELECT FRIEND_ID FROM USERS AS U
            LEFT OUTER JOIN USER_FRIENDS AS UF ON U.USER_ID=UF.USER_ID
            WHERE U.USER_ID = 1 --Id второго пользователя Пользователя, друзей которого ищем
    ) AS UF_IDS
    JOIN USERS AS U ON UF_IDS.FRIEND_ID=U.USER_ID
) AS U2_FRIENDS
ON U1_FRIENDS.USER_ID=U2_FRIENDS.USER_ID

-- возвращает список из первых 10 фильмов по количеству лайков.

SELECT F.NAME, COUNT(DISTINCT FL.USER_ID) AS LIKES_COUNT
FROM FILMS AS F
JOIN FILM_LIKES AS FL ON F.FILM_ID = FL.FILM_ID
GROUP BY F.FILM_ID
ORDER BY LIKES_COUNT DESC
LIMIT 10;