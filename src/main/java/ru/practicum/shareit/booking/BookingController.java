package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.UserHeader;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(UserHeader.OWNER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForCurrentUser(@RequestHeader(UserHeader.OWNER_ID) Long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String state,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllBookingsForBookerOrItemOwner(userId, state, from, size, true);
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(UserHeader.OWNER_ID) Long userId, @RequestBody @Valid BookingSimplyDto booking) {
        return bookingService.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto answerBookingRequest(@RequestHeader(UserHeader.OWNER_ID) Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam(name = "approved") Boolean approved) {
        if (approved) {
            return bookingService.approveBookingRequest(userId, bookingId);
        } else {
            return bookingService.rejectBookingRequest(userId, bookingId);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForCurrentOwner(@RequestHeader(UserHeader.OWNER_ID) Long userId,
                                                       @RequestParam(name = "state", defaultValue = "all") String state,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return bookingService.getAllBookingsForBookerOrItemOwner(userId, state, from, size, false);
    }
}
