package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.EventType;
import ru.yandex.practicum.filmorate.constants.Operation;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public Collection<Event> findAllEventsByUserId(int id) {
        checkUser(id);
        return userStorage.findAllEventsByUserId(id);
    }

    public void addEvent(int userId, EventType eventType, Operation operation, int entityId) {
        userStorage.addEvent(userId, eventType, operation, entityId);
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
        addEvent(id, EventType.FRIEND, Operation.ADD, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        checkUser(id);
        checkUser(friendId);
        userStorage.deleteFriend(id, friendId);
        addEvent(id, EventType.FRIEND, Operation.REMOVE, friendId);
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
