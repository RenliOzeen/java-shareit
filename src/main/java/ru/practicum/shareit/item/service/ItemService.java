package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;

    public List<Item> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId);
    }

    public Item getItem(Long itemId) {
        return itemStorage.getItem(itemId);
    }

    public Item addItem(Long userId, Item item) {
        return itemStorage.addItem(userId, item);
    }

    public Item updateItem(Long userId, Long itemId, Item item) {
        return itemStorage.updateItem(userId, itemId, item);
    }

    public Boolean deleteItem(Long itemId) {
        return itemStorage.deleteItem(itemId);
    }

    public List<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
