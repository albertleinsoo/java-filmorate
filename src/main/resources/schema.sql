DROP TABLE IF EXISTS DIRECTOR,FILM_DIRECTOR, REVIEWS_LIKES, REVIEWS, USERS, FILMS, USER_FRIENDS, FILM_LIKES, FILM_GENRE, GENRE, RATINGS, EVENTS;

create table GENRE
(
    GENRE_ID BIGINT            not null
        primary key,
    NAME     CHARACTER VARYING not null
        unique
);

create table RATINGS
(
    RATING_ID   BIGINT            not null
        primary key,
    RATING_NAME CHARACTER VARYING not null
);

create table FILMS
(
    FILM_ID      BIGINT auto_increment
        primary key,
    NAME         CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE              not null,
    DURATION     INTEGER           not null,
    RATING_ID    BIGINT,
    RATE         INTEGER,
    constraint FOREIGN_KEY_NAME
        foreign key (RATING_ID) references RATINGS
);

create table FILM_GENRE
(
    FILM_GENRE_ID BIGINT auto_increment
        primary key,
    FILM_ID       BIGINT not null,
    GENRE_ID      BIGINT not null,
    constraint FILM_GENRE_UK
        unique (FILM_ID, GENRE_ID),
    constraint FILM_GENRE_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint FILM_GENRE_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE ON DELETE CASCADE
);

create table USERS
(
    USER_ID  BIGINT auto_increment
        primary key,
    EMAIL    CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null,
    NAME     CHARACTER VARYING,
    BIRTHDAY DATE              not null
);

create table FILM_LIKES
(
    ID      BIGINT auto_increment
        primary key,
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint FILM_LIKES_UK
        unique (FILM_ID, USER_ID),
    constraint FILM_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint FILM_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS ON DELETE CASCADE
);

create table USER_FRIENDS
(
    USER_FRIENDS_ID     BIGINT auto_increment
        primary key,
    USER_ID             BIGINT                not null,
    FRIEND_ID           BIGINT                not null,
    CONFIRMED_BY_FRIEND CHARACTER VARYING(20) not null,
    constraint USER_FRIENDS_UK
        unique (USER_ID, FRIEND_ID),
    constraint USER_FRIENDS_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS ON DELETE CASCADE,
    constraint USER_FRIENDS_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS ON DELETE CASCADE
);

create table if not exists REVIEWS
(
    REVIEW_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    CONTENT   CHARACTER VARYING(254) not null,
    IS_POSITIVE boolean,
    USER_ID BIGINT not null,
    FILM_ID BIGINT not null,
    USEFUL BIGINT,

    constraint USER_FILM_UK
        unique (USER_ID, FILM_ID),
    constraint REVIEWS_USER_ID_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS ON DELETE CASCADE,
    constraint REVIEWS_FILM_ID_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE
);

create table if not exists REVIEWS_LIKES
(
    REVIEW_ID BIGINT,
    USER_ID BIGINT,
    LIKED boolean,

    constraint REVIEWS_LIKES_PK
        primary key (REVIEW_ID, USER_ID),
    constraint REVIEWS_LIKES_USER_ID_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS on delete cascade,
    constraint REVIEWS_LIKES_REVIEW_ID_REVIEWS_REVIEW_ID_FK
        foreign key (REVIEW_ID) references REVIEWS on delete cascade
);

CREATE TABLE events (
	event_id BIGINT NOT NULL AUTO_INCREMENT,
	time_stamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	user_id BIGINT NOT NULL,
	operation ENUM('ADD', 'REMOVE', 'UPDATE') NOT NULL,
	event_type ENUM('FRIEND', 'LIKE', 'REVIEW') NOT NULL,
	entity_id BIGINT NOT NULL,

	CONSTRAINT pk_events
		PRIMARY KEY (event_id),

	CONSTRAINT fk_events_user_id
    	FOREIGN KEY (user_id)
    	REFERENCES users (user_id)
    	ON DELETE CASCADE
);


create table DIRECTOR
(
    DIRECTOR_ID BIGINT auto_increment
        primary key,
    NAME     CHARACTER VARYING
        unique
);

create table FILM_DIRECTOR
(
    FILM_DIRECTOR_ID BIGINT auto_increment
        primary key,
    FILM_ID       BIGINT not null,
    DIRECTOR_ID      BIGINT not null,
    constraint FILM_DIRECTOR_UK
        unique (FILM_ID, DIRECTOR_ID),
    constraint FILM_DIRECTOR_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint FILM_DIRECTOR_DIRECTOR_DIRECTOR_ID_FK
        foreign key (DIRECTOR_ID) references DIRECTOR ON DELETE CASCADE
);
