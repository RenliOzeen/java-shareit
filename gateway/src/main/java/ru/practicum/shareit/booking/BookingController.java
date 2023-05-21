package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.UnknownStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Arrays;

@Controller
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody @Valid BookItemRequest bookItemRequest) {
        log.info("Create booking {} by userId={}", bookItemRequest, userId);
        return bookingClient.save(userId, bookItemRequest);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> update(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "approved") boolean isApprove) {
        log.info("Update bookingId={}", bookingId);
        return bookingClient.update(bookingId, userId, isApprove);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId) {
        log.info("Find booking {}, userId={}", bookingId, userId);
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state", defaultValue = "all") String state,
                                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(defaultValue = "10") Integer size) {

        BookingState bookingState = Arrays.stream(BookingState.values()).filter(s -> s.name().equals(state.toUpperCase())).findFirst()
                .orElseThrow(() -> new UnknownStateException("Unknown state: " + state));
        log.info("get all bookings for current user with bookingState {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getAllBookingsForCurrentUser(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForCurrentOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(defaultValue = "ALL") String state,
                                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(defaultValue = "10") Integer size) {

        BookingState bookingState = Arrays.stream(BookingState.values()).filter(s -> s.name().equals(state.toUpperCase())).findFirst()
                .orElseThrow(() -> new UnknownStateException("Unknown state: " + state));
        log.info("Get all bookings by userId={}", userId);
        return bookingClient.getAllBookingsForCurrentOwner(userId, bookingState, from, size);
    }
}
