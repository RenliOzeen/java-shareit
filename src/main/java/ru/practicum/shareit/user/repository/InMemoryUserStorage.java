package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private long lastIdNumber = 0;
    private final HashMap<Long, User> users;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found");
        }
        return users.get(userId);
    }

    @Override
    public User addUser(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new AlreadyExistsException("User already exists");
        }
        user.setId(lastIdNumber + 1);
        users.put(user.getId(), user);
        lastIdNumber++;
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found");
        }
        User updateUser = User.builder()
                .id(userId)
                .name(user.getName() != null ? user.getName() : users.get(userId).getName())
                .email(user.getEmail() != null ? user.getEmail() : users.get(userId).getEmail())
                .build();
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), userId))) {
            throw new AlreadyExistsException("User already exists");
        }
        users.put(userId, updateUser);
        return updateUser;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found");
        }
        users.remove(userId);
        return users.containsKey(userId);
    }
}
