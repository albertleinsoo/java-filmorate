create table IF NOT EXISTS RATINGS
(
    RATING_ID   BIGINT            not null
    primary key,
    RATING_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS GENRE
(
    GENRE_ID BIGINT            not null
    primary key,
    NAME     CHARACTER VARYING not null
    unique
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      BIGINT            not null
        primary key,
    NAME         CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE              not null,
    DURATION     INTEGER           not null,
    RATING_ID    BIGINT,
    constraint FOREIGN_KEY_NAME
        foreign key (RATING_ID) references RATINGS
);

create table IF NOT EXISTS FILM_GENRE
(
    FILM_GENRE_ID BIGINT not null
        primary key,
    FILM_ID       BIGINT not null,
    GENRE_ID      BIGINT not null,
    constraint FILM_GENRE_UK
        unique (FILM_ID, GENRE_ID),
    constraint FILM_GENRE_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILM_GENRE_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);

create table IF NOT EXISTS USERS
(
    USER_ID  BIGINT            not null
        primary key,
    EMAIL    CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null,
    NAME     CHARACTER VARYING,
    BIRTHDAY DATE              not null
);

create table IF NOT EXISTS FILM_LIKES
(
    ID      BIGINT not null
        primary key,
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint FILM_LIKES_UK
        unique (FILM_ID, USER_ID),
    constraint FILM_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILM_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

create table IF NOT EXISTS USER_FRIENDS
(
    USER_FRIENDS_ID     BIGINT  not null
        primary key,
    USER_ID             BIGINT  not null,
    FRIEND_ID           BIGINT  not null,
    CONFIRMED_BY_FRIEND BOOLEAN not null,
    constraint USER_FRIENDS_UK
        unique (USER_ID, FRIEND_ID),
    constraint USER_FRIENDS_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS,
    constraint USER_FRIENDS_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
);