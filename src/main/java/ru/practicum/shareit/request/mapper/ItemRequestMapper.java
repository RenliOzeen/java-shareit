package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestOwnerId(itemRequest.getRequestor().getId())
                .items(new ArrayList<>())
                .created(itemRequest.getCreateDate())
                .build();
    }
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .createDate(itemRequestDto.getCreated())
                .build();
    }

    public static List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequestList) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        itemRequestList.forEach(i -> itemRequestDtoList.add(toItemRequestDto(i)));
        return itemRequestDtoList;
    }
}
