package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private Item item;
    private ItemRequest itemRequest;
    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .requestId(1L)
                .description("item description")
                .available(true)
                .build();

        User user = User.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .request(itemRequest)
                .owner(user)
                .available(true)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestOwnerId(user.getId())
                .description("description")
                .created(LocalDateTime.now())
                .items(List.of(itemDto))
                .build();
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
    }

    @Test
    void shouldReturnItemDto() {
        ItemDto result = ItemMapper.toItemDto(item);
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
    }

    @Test
    void shouldReturnItemWithCommentDto() {
        ItemWithCommentDto result = ItemMapper.toItemWithCommentDto(item);
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getComments(), Collections.emptyList());
    }

    @Test
    void shouldReturnItem() {
        Item result = ItemMapper.toItem(itemDto);
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
    }

    @Test
    void shouldReturnItemDtoList() {
        List<ItemDto> result = ItemMapper.toItemDtoList(List.of(item));
        assertNotNull(result.get(0));
        assertEquals(result.get(0).getId(), item.getId());
    }

    @Test
    void shouldReturnItemWithCommentDtoList() {
        List<ItemWithCommentDto> result = ItemMapper.toItemWithCommentDtoList(List.of(item));
        assertNotNull(result.get(0));
        assertEquals(result.get(0).getId(), item.getId());
        assertEquals(result.get(0).getComments(), Collections.emptyList());
    }
}