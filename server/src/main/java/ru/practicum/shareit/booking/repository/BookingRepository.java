package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(Long bookerId, LocalDateTime start,
                                                                         LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartDateIsAfter(Long bookerId, LocalDateTime start,
                                                                           PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusEquals(Long bookerId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerId(Long ownerId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(Long ownerId, LocalDateTime start,
                                                                            LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndDateIsBefore(Long ownerId, LocalDateTime start, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartDateIsAfter(Long ownerId, LocalDateTime start, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(Long ownerId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndDateIsBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime endDate);

    List<Booking> findAllByStartDateIsAfterAndItemIdAndStatusOrderByStartDateAsc(LocalDateTime startDate, Long itemId, BookingStatus status);

    List<Booking> findAllByStartDateIsBeforeAndItemIdAndStatusOrderByStartDateDesc(LocalDateTime startDate, Long itemId, BookingStatus status);

    List<Booking> getBookingsByItemIdInOrderByStartDateAsc(List<Long> itemIdList);
}
