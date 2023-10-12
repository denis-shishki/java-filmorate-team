package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.constants.EventType;
import ru.yandex.practicum.filmorate.constants.Operation;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    void addEvent(int userId, EventType eventType, Operation operation, int entityId);

    Collection<Event> findAllEventsByUserId(int userId);

    Collection<User> findAllUsers();

    List<Integer> findAllUserIds();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    Optional<User> findUser(int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    Collection<User> findAllFriends(int id);

    Collection<User> findCommonFriends(int id, int otherId);

}
