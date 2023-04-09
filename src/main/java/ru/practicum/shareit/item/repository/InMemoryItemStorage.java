package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private long lastIdNumber = 0;
    private final HashMap<Long, Item> items;

    @Override
    public List<Item> getAllItems(Long userId) {
        return items.values().stream().filter(i -> Objects.equals(i.getOwnerId(), userId)).collect(Collectors.toList());
    }

    @Override
    public Item getItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found");
        }
        return items.get(itemId);
    }

    @Override
    public Item addItem(Long userId, Item item) {
        item.setId(lastIdNumber + 1);
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        lastIdNumber++;
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found");
        }
        if (item.getAvailable() == null) {
            item.setAvailable(true);
        }
        Item updateItem = Item.builder()
                .id(item.getId() != null ? item.getId() : itemId)
                .name(item.getName() != null ? item.getName() : items.get(itemId).getName())
                .available(item.getAvailable() != null ? item.getAvailable() : items.get(itemId).getAvailable())
                .description(item.getDescription() != null ? item.getDescription() : items.get(itemId).getDescription())
                .ownerId(item.getOwnerId() != null ? item.getOwnerId() : items.get(itemId).getOwnerId())
                .build();

        if (!Objects.equals(items.get(itemId).getOwnerId(), userId)) {
            throw new NotFoundException("User for this item was not found");
        }
        items.put(updateItem.getId(), updateItem);
        return updateItem;
    }

    @Override
    public Boolean deleteItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found");
        }
        items.remove(itemId);
        return items.containsKey(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream().filter(i -> ((i.getName() != null && i.getName().contains(text.substring(1).toLowerCase()))
                        || (i.getDescription() != null && i.getDescription().contains(text.substring(1).toLowerCase())))
                        && i.getAvailable())
                .collect(Collectors.toList());
    }
}
