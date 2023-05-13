package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USERID_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForCurrentUser(@RequestHeader(USERID_HEADER) Long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String state,
                                                         @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBookingsForBookerOrItemOwner(userId, state, from, size, true);
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(USERID_HEADER) Long userId, @RequestBody @Valid BookingSimplyDto booking) {
        return bookingService.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto answerBookingRequest(@RequestHeader(USERID_HEADER) Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam(name = "approved") Boolean approved) {
        if (approved) {
            return bookingService.approveBookingRequest(userId, bookingId);
        } else {
            return bookingService.rejectBookingRequest(userId, bookingId);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForCurrentOwner(@RequestHeader(USERID_HEADER) Long userId,
                                                       @RequestParam(name = "state", defaultValue = "all") String state,
                                                       @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

        return bookingService.getAllBookingsForBookerOrItemOwner(userId, state, from, size, false);
    }
}
