package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingSimplyDto toBookingSimplyDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingSimplyDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static Booking toBooking(BookingSimplyDto bookingSimplyDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingSimplyDto.getId())
                .startDate(bookingSimplyDto.getStart())
                .endDate(bookingSimplyDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> bookingList) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        bookingList.forEach(i -> bookingDtoList.add(toBookingDto(i)));
        return bookingDtoList;
    }
}
