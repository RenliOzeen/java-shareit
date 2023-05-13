package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private BookingSimplyDto bookingSimplyDto;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();

        bookingSimplyDto = BookingSimplyDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-06-12 00:00", formatter))
                .end(LocalDateTime.parse("2023-07-12 00:00", formatter))
                .itemId(1L)
                .bookerId(1L)
                .build();
    }

    @Test
    void shouldAddBookingOrThrowException() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(new Booking());
        BookingDto bookingDto = bookingService.addBooking(2L, bookingSimplyDto);

        assertNotNull(bookingDto);

        bookingSimplyDto.setEnd(LocalDateTime.now());
        assertThrows(InvalidArgumentsException.class, () -> bookingService.addBooking(2L, bookingSimplyDto));

        bookingSimplyDto.setEnd(LocalDateTime.parse("2023-07-12 00:00", formatter));
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(1L, bookingSimplyDto));

        user.setId(2L);
        item.setOwner(user);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        assertThrows(UserValidationException.class, () -> bookingService.addBooking(2L, bookingSimplyDto));

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        item.setAvailable(false);

        assertThrows(InvalidArgumentsException.class, () -> bookingService.addBooking(2L, bookingSimplyDto));
    }

    @Test
    void shouldApproveAndRejectBookingOrThrowException() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto);
        Booking booking = BookingMapper.toBooking(bookingSimplyDto, item, user);
        item.setOwner(user);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(new Booking());

        assertThrows(NotFoundException.class, () -> bookingService.approveBookingRequest(2L, 1L));
        assertThrows(NotFoundException.class, () -> bookingService.rejectBookingRequest(2L, 1L));

        BookingDto bookingDto = bookingService.rejectBookingRequest(1L, 1L);
        assertNotNull(bookingDto);
        assertEquals(bookingDto.getStatus(), BookingStatus.REJECTED);

        bookingDto = bookingService.approveBookingRequest(1L, 1L);
        assertNotNull(bookingDto);
        assertEquals(bookingDto.getStatus(), BookingStatus.APPROVED);


        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        assertThrows(UserValidationException.class, () -> bookingService.approveBookingRequest(2L, 1L));

        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(InvalidArgumentsException.class, () -> bookingService.rejectBookingRequest(1L, 1L));

        user.setId(2L);
        item.setOwner(user);
        booking.setItem(item);
        assertThrows(InvalidArgumentsException.class, () -> bookingService.approveBookingRequest(2L, 1L));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ALL, 1, 2, true",
            "CURRENT, -1, 1, true",
            "PAST, -2, -1, true",
            "FUTURE, 1, 2, true",
            "WAITING, 1, 2, true",
            "REJECTED, 1, 2, true",
            "ALL, 1, 2, false",
            "CURRENT, -1, 1, false",
            "PAST, -2, -1, false",
            "FUTURE, 1, 2, false",
            "WAITING, 1, 2, false",
            "REJECTED, 1, 2, false"
    })
    void shouldReturnAllBookingForBookerOrItemOwner(String state, int hoursStart, int hoursEnd, boolean isBooker) {
        LocalDateTime start = LocalDateTime.now().plusHours(hoursStart);
        LocalDateTime end = LocalDateTime.now().plusHours(hoursEnd);
        User user1 = UserMapper.toUser(userDto);
        User user2 = UserMapper.toUser(userDto);
        user2.setId(2L);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user1);

        Booking booking = BookingMapper.toBooking(bookingSimplyDto, item, user1);
        booking.setBooker(user1);
        item.setOwner(user2);
        booking.setItem(item);

        booking.setStartDate(start);
        booking.setEndDate(end);

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(Mockito.anyLong(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByBookerIdAndStartDateIsAfter(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByBookerIdAndEndDateIsBefore(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByBookerIdAndStatusEquals(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByItemOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(Mockito.anyLong(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> bookings;
        if (isBooker) {
            bookings = bookingService.getAllBookingsForBookerOrItemOwner(user2.getId(), state, 0, 10, true);
        } else {
            bookings = bookingService.getAllBookingsForBookerOrItemOwner(user1.getId(), state, 0, 10, false);
        }
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getId(), 1L);
    }

    @Test
    void shouldGetBookingOrThrowException() {
        User user1 = UserMapper.toUser(userDto);
        User user2 = UserMapper.toUser(userDto);
        user2.setId(2L);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user1);

        Booking booking = BookingMapper.toBooking(bookingSimplyDto, item, user1);
        booking.setBooker(user1);
        item.setOwner(user2);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(3L, 1L));

        User user3 = UserMapper.toUser(userDto);
        user3.setId(3L);
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(3L, 1L));

        assertNotNull(bookingService.getBooking(1L, 1L));
    }
}