package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {
    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);

        user = User.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .createDate(LocalDateTime.now())
                .description("description")
                .requestor(user)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("description")
                .requestOwnerId(user.getId())
                .items(List.of(itemDto))
                .build();
    }

    @Test
    void shouldReturnItemRequestDto() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(itemRequest);
        assertNotNull(result);
        assertEquals(result.getRequestOwnerId(), user.getId());
    }

    @Test
    void shouldReturnItemRequest() {
        ItemRequest result = ItemRequestMapper.toItemRequest(itemRequestDto);
        assertNotNull(result);
        assertEquals(result.getId(), 1L);
    }

    @Test
    void shouldReturnItemRequestDtoList() {
        List<ItemRequestDto> result = ItemRequestMapper.toItemRequestDtoList(List.of(itemRequest));
        assertNotNull(result.get(0));
        assertEquals(result.get(0).getRequestOwnerId(), user.getId());
    }
}