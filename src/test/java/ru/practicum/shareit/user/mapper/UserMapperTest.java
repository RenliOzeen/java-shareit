package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .email("email@email.ru")
                .name("name")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("email@email.ru")
                .name("name")
                .build();
    }

    @Test
    void shouldReturnUserDto() {
        UserDto result = UserMapper.toUserDto(user);
        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
    }

    @Test
    void shouldReturnUser() {
        User result = UserMapper.toUser(userDto);
        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
    }

    @Test
    void shouldReturnUserDtoList() {
        List<UserDto> result = UserMapper.toUserDtoList(List.of(user));
        assertNotNull(result.get(0));
        assertEquals(result.get(0).getId(), user.getId());
    }
}