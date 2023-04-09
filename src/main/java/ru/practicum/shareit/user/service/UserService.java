package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(Long userId, User user) {
        return userStorage.updateUser(userId, user);
    }

    public Boolean deleteUser(Long userId) {
        return userStorage.deleteUser(userId);
    }
}
