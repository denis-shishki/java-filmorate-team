drop all objects;

create table if not exists mpa
(
    mpa_id   integer not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name VARCHAR(20)
);

create table if not exists films
(
    film_id      integer      not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name    varchar(100) NOT NULL,
    description  varchar(200) NOT NULL,
    release_date date         NOT NULL CHECK (release_date > '1895-12-28'),
    duration     integer      NOT NULL CHECK (duration > 0),
    mpa_id       integer,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);

create table if not exists users
(
    user_id  integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar(50) NOT NULL UNIQUE,
    login    varchar(50) NOT NULL UNIQUE,
    name     varchar(50),
    birthday date        NOT NULL
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id   integer NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_director
(
    film_id     integer NOT NULL,
    director_id integer NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES directors (director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

create table if not exists genres
(
    genre_id   integer     not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar(50) NOT NULL
);

create table if not exists film_genre
(
    film_id  integer not null,
    genre_id integer not null,
    foreign key (film_id) references films (film_id) on delete cascade,
    foreign key (genre_id) references genres (genre_id) on delete cascade,
    primary key (film_id, genre_id)
);

create table if not exists friends
(
    user_id   integer not null,
    friend_id integer not null,
    foreign key (user_id) references users (user_id) on delete cascade,
    foreign key (friend_id) references users (user_id) on delete cascade,
    primary key (user_id, friend_id)
);

create table if not exists likes
(
    film_id integer not null,
    user_id integer not null,
    foreign key (film_id) references films (film_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade,
    primary key (film_id, user_id)
);

create table if not exists operations
(
    operation_id   integer     NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    operation_name varchar(50) NOT NULL
);

create table if not exists event_types
(
    event_type_id   integer     NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_type_name varchar(50) NOT NULL
);

create table if not exists events
(
    event_id      integer not null GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id       integer NOT NULL,
    event_type_id integer NOT NULL,
    operation_id  integer NOT NULL,
    entity_id     integer NOT NULL,
    timestamp     long    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (event_type_id) REFERENCES event_types (event_type_id),
    FOREIGN KEY (operation_id) REFERENCES operations (operation_id)
);
