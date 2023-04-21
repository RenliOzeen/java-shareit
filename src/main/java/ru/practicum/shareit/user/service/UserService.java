package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userStorage.getAllUsers());
    }

    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    public UserDto addUser(UserDto user) {
        return UserMapper.toUserDto(userStorage.addUser(UserMapper.toUser(user)));
    }

    public UserDto updateUser(Long userId, UserDto user) {
        return UserMapper.toUserDto(userStorage.updateUser(userId, UserMapper.toUser(user)));
    }

    public Boolean deleteUser(Long userId) {
        return userStorage.deleteUser(userId);
    }
}
