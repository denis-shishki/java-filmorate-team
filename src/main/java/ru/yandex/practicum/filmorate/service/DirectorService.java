package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> findAllDirectors() {
        return directorStorage.findAllDirectors();
    }

    public Director findDirectorById(Integer directorId) {
        return directorStorage.findDirectorById(directorId).orElseThrow(
                () -> new DirectorNotFoundException("Режиссёр не найден.")
        );
    }

    public Director createDirector(Director director) {
        validateDirector(director);
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        validateDirector(director);
        checkDirector(director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(Integer directorId) {
        checkDirector(directorId);
        directorStorage.deleteDirector(directorId);
    }

    public void setDirectors(List<Film> films) {
        directorStorage.setDirectors(films);
    }

    private void validateDirector(Director director) {
        if (director.getName().isEmpty()) {
            throw new ValidationException("Имя режиссёра не может быть пустым.");
        }
    }

    private void checkDirector(int directorId) {
        if (directorStorage.findDirectorById(directorId).isEmpty()) {
            throw new DirectorNotFoundException("Режиссёр не найден.");
        }
    }
}
