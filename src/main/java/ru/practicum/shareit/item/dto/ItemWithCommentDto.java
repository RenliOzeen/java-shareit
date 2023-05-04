package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithCommentDto {
    Long id;
    @NotNull @NotEmpty String name;
    @NotNull String description;
    @NotNull Boolean available;
    Long ownerId;
    ItemRequest request;

    BookingSimplyDto nextBooking;

    BookingSimplyDto lastBooking;
    List<CommentDto> comments;
}
