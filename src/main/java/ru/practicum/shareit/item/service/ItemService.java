package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<ItemDto> getAllItems(Long userId) {
        isUserExists(userId);
        return ItemMapper.toItemDtoList(itemStorage.getAllItems(userId));
    }

    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    public ItemDto addItem(Long userId, ItemDto item) {
        isUserExists(userId);
        return ItemMapper.toItemDto(itemStorage.addItem(userId, ItemMapper.toItem(item)));
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        isUserExists(userId);
        return ItemMapper.toItemDto(itemStorage.updateItem(userId, itemId, ItemMapper.toItem(item)));
    }

    public Boolean deleteItem(Long itemId) {
        return itemStorage.deleteItem(itemId);
    }

    public List<ItemDto> searchItems(String text) {
        return ItemMapper.toItemDtoList(itemStorage.searchItems(text));
    }

    private void isUserExists(Long userId) {
        userStorage.getUser(userId);
    }
}
