package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Optional<Director> findDirector(int directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int directorId);

    void setDirectors(List<Film> films);
}
