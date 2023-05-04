package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingSimplyDto booking);

    BookingDto approveBookingRequest(Long userId, Long bookingId);

    BookingDto rejectBookingRequest(Long userId, Long bookingId);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsForBookerOrItemOwner(Long userId, String state, boolean isBooker);

}
