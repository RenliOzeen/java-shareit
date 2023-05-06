package ru.practicum.shareit.item.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemWithCommentDto toItemWithCommentDto(Item item) {
        return ItemWithCommentDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(new ArrayList<>())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemList.forEach(i -> itemDtoList.add(toItemDto(i)));
        return itemDtoList;
    }

    public static List<ItemWithCommentDto> toItemWithCommentDtoList(List<Item> itemList) {
        List<ItemWithCommentDto> itemWithCommentDtoList = new ArrayList<>();
        itemList.forEach(i -> itemWithCommentDtoList.add(toItemWithCommentDto(i)));
        return itemWithCommentDtoList;
    }
}