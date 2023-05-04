package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(Long bookerId, LocalDateTime start,
                                                                                             LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndDateIsBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartDateIsAfterOrderByStartDateDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDateDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndDateIsBeforeOrderByStartDateDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStartDateIsAfterOrderByStartDateDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusEqualsOrderByStartDateDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime endDate);

    List<Booking> findAllByStartDateIsAfterAndItemIdAndStatusOrderByStartDateAsc(LocalDateTime startDate, Long itemId, BookingStatus status);

    List<Booking> findAllByStartDateIsBeforeAndItemIdAndStatusOrderByStartDateDesc(LocalDateTime startDate, Long itemId, BookingStatus status);
}
