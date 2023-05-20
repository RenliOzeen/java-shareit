package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    UserService userService;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();
        user = UserMapper.toUser(userDto);
    }

    @Test
    void shouldGetAllUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> result = userService.getAllUsers();
        assertNotNull(result);
    }

    @Test
    void shouldGetUserOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> userService.getUser(2L));

        UserDto result = userService.getUser(user.getId());
        assertNotNull(result);
    }

    @Test
    void shouldAddUser() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        UserDto result = userService.addUser(userDto);

        assertNotNull(result);
    }

    @Test
    void shouldUpdateUserOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> userService.updateUser(2L, userDto));
        userDto.setName("update");
        user.setName("update");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        UserDto result = userService.updateUser(user.getId(), userDto);

        assertNotNull(result);
        assertEquals(result.getName(), "update");
    }

    @Test
    void shouldDeleteUserOrThrowException() {
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> userService.deleteUser(2L));

        userService.deleteUser(user.getId());
        Mockito.verify(userRepository).deleteById(user.getId());
    }
}