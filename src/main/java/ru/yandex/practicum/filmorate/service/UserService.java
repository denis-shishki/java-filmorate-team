package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final DirectorService directorService;

    public List<Integer> findAllUserIds() {
        return userStorage.findAllUserIds();
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public List<Film> findFilmsRecommendationsForUser(int userId) {
        findUser(userId);

        List<Integer> likeFilmIdsByUser = filmStorage.findLikeFilmIdsByUserId(userId);
        if (likeFilmIdsByUser == null) return new ArrayList<>();

        List<Integer> allUserIds = findAllUserIds();

        int secondUserId = 0;
        int matches = 0;

        for (int id : allUserIds) {
            if (id == userId) continue;

            List<Integer> filmIds = filmStorage.findLikeFilmIdsByUserId(id);
            List<Integer> commonElements = new ArrayList<>(likeFilmIdsByUser);
            commonElements.retainAll(filmIds);

            int numOfCommonElements = commonElements.size();

            if (numOfCommonElements > matches) {
                matches = numOfCommonElements;
                secondUserId = id;
            }
        }

        if (matches == 0) return new ArrayList<>();

        List<Integer> likeFilmIdsBySecondUser = filmStorage.findLikeFilmIdsByUserId(secondUserId);
        likeFilmIdsBySecondUser.removeAll(likeFilmIdsByUser);

        List<Film> recommendFilms = filmStorage.findFilmsByIds(likeFilmIdsBySecondUser);
        genreService.setGenres(recommendFilms);
        directorService.setDirectors(recommendFilms);
        return recommendFilms;
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        checkUser(user.getId());
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(int userId) {
        checkUser(userId);
        userStorage.deleteUser(userId);
    }

    public User findUser(int userId) {
        return userStorage.findUser(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден")
        );
    }

    public void addFriend(int id, int friendId) {
        checkUser(id);
        checkUser(friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        checkUser(id);
        checkUser(friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> findAllFriends(int id) {
        checkUser(id);
        return userStorage.findAllFriends(id);
    }

    public Collection<User> findCommonFriends(int id, int otherId) {
        checkUser(id);
        checkUser(otherId);
        return userStorage.findCommonFriends(id, otherId);
    }

    private void validateUser(User user) {
        if (user.getEmail().isEmpty() || !(user.getEmail().contains("@"))) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkUser(int id) {
        if (userStorage.findUser(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
    }
}
