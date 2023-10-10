package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    void addEvent(Event event);

    Collection<Event> findAllEventsByUserId(int userId);

    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    Optional<User> findUser(int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    Collection<User> findAllFriends(int id);

    Collection<User> findCommonFriends(int id, int otherId);

}
