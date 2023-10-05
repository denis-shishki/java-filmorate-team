package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> findAllDirectors();

    Optional<Director> findDirectorById(Integer directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer directorId);

    void setDirectors(List<Film> films);
}
