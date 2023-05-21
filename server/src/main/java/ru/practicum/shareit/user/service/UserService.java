package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userStorage;

    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userStorage.findAll());
    }

    public UserDto getUser(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto addUser(UserDto user) {
        return UserMapper.toUserDto(userStorage.save(UserMapper.toUser(user)));
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto user) {
        User savedUser = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("this user was not found"));
        User updateUser = User.builder()
                .id(userId)
                .name(user.getName() != null ? user.getName() : savedUser.getName())
                .email(user.getEmail() != null ? user.getEmail() : savedUser.getEmail())
                .build();

        return UserMapper.toUserDto(userStorage.save(updateUser));
    }

    @Transactional
    public Boolean deleteUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User was not found");
        }
        userStorage.deleteById(userId);
        return !userStorage.existsById(userId);
    }
}
