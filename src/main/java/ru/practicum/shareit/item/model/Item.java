package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Item {

    Long id;
    Long ownerId;
    @NotNull
    @NotEmpty
    String name;
    @NotNull
    String description;
    ItemRequest request;
    @NotNull
    Boolean available;
}
