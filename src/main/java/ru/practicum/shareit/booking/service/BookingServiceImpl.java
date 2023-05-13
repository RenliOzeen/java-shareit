package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.exceptions.UserValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, BookingSimplyDto booking) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item was not found"));
        if (!item.getAvailable()) {
            throw new InvalidArgumentsException("Item is unavailable");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart())) {
            throw new InvalidArgumentsException("End date before start date");
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new UserValidationException("Booker is the owner of item");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(booking, item, user)));
    }

    @Override
    @Transactional
    public BookingDto approveBookingRequest(Long userId, Long bookingId) {
        checkUserExists(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking was not found"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new UserValidationException("This user is not the owner for this item");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new InvalidArgumentsException("Booking already approved");
        }
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto rejectBookingRequest(Long userId, Long bookingId) {
        checkUserExists(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking was not found"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new InvalidArgumentsException("This user is not the owner for this item");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new InvalidArgumentsException("This booking was approved");
        }
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        checkUserExists(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking was not found"));
        if (!(Objects.equals(booking.getItem().getOwner().getId(), userId)
                || Objects.equals(booking.getBooker().getId(), userId))) {
            throw new NotFoundException("This user is not the owner for this item");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsForBookerOrItemOwner(Long userId, String state, Integer from, Integer size, boolean isBooker) {
        if (from < 0 || size <= 0) {
            throw new InvalidArgumentsException("'from' and 'size' should be positive");
        }
        checkUserExists(userId);
        try {
            BookingState.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by("startDate").descending());
        switch (BookingState.valueOf(state.toUpperCase())) {
            case ALL:
                return isBooker ? BookingMapper.toBookingDtoList(bookingRepository.findAllByBookerId(userId, pageRequest))
                        : BookingMapper.toBookingDtoList(bookingRepository.findAllByItemOwnerId(userId, pageRequest));
            case CURRENT:
                return isBooker ? BookingMapper.toBookingDtoList(bookingRepository.findAllByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest))
                        : BookingMapper.toBookingDtoList(bookingRepository.findAllByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest));
            case FUTURE:
                return isBooker ? BookingMapper.toBookingDtoList(bookingRepository.findAllByBookerIdAndStartDateIsAfter(userId, LocalDateTime.now(), pageRequest))
                        : BookingMapper.toBookingDtoList(bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(userId, LocalDateTime.now(), pageRequest));
            case PAST:
                return isBooker ? BookingMapper.toBookingDtoList(bookingRepository.findAllByBookerIdAndEndDateIsBefore(userId, LocalDateTime.now(), pageRequest))
                        : BookingMapper.toBookingDtoList(bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(userId, LocalDateTime.now(), pageRequest));
            case WAITING:
                return isBooker ? BookingMapper.toBookingDtoList(bookingRepository.findAllByBookerIdAndStatusEquals(userId, BookingStatus.WAITING, pageRequest))
                        : BookingMapper.toBookingDtoList(bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, BookingStatus.WAITING, pageRequest));
            case REJECTED:
                return isBooker ? BookingMapper.toBookingDtoList(bookingRepository.findAllByBookerIdAndStatusEquals(userId, BookingStatus.REJECTED, pageRequest))
                        : BookingMapper.toBookingDtoList(bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, BookingStatus.REJECTED, pageRequest));
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User was not found"));
    }
}
