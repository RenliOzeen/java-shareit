package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private Booking booking;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private BookingSimplyDto bookingSimplyDto;
    private User user;
    private Item item;

    @BeforeEach
    void setup() {
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(true)
                .build();

        user = User.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();

        booking = Booking.builder()
                .id(1L)
                .startDate(LocalDateTime.parse("2023-06-12 00:00", formatter))
                .endDate(LocalDateTime.parse("2023-07-12 00:00", formatter))
                .item(item)
                .booker(user)
                .build();

        bookingSimplyDto = BookingSimplyDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-06-12 00:00", formatter))
                .end(LocalDateTime.parse("2023-07-12 00:00", formatter))
                .itemId(item.getId())
                .bookerId(user.getId())
                .build();
    }

    @Test
    void shouldReturnBookingDto() {
        BookingDto result = BookingMapper.toBookingDto(booking);
        assertEquals(result.getId(), 1L);
        assertEquals(result.getItem(), item);
        assertEquals(result.getBooker(), user);
    }

    @Test
    void shouldReturnBookingSimplyDto() {
        BookingSimplyDto result = BookingMapper.toBookingSimplyDto(booking);
        assertEquals(result.getId(), 1L);
        assertEquals(result.getItemId(), item.getId());
        assertEquals(result.getBookerId(), user.getId());
    }

    @Test
    void shouldReturnBooking() {
        Booking result = BookingMapper.toBooking(bookingSimplyDto, item, user);
        assertEquals(result.getId(), 1L);
        assertEquals(result.getItem(), item);
        assertEquals(result.getBooker(), user);
    }

    @Test
    void shouldReturnBookingDtoList() {
        List<BookingDto> result = BookingMapper.toBookingDtoList(List.of(booking));
        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getItem(), item);
        assertEquals(result.get(0).getBooker(), user);
    }
}