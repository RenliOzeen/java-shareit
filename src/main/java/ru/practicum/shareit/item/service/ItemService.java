package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private final ItemRequestRepository itemRequestRepository;

    public List<ItemWithCommentDto> getAllItems(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("This user was not found");
        }
        List<ItemWithCommentDto> itemWithCommentDtoList = ItemMapper.toItemWithCommentDtoList(itemStorage.findAllByOwnerId(userId));
        List<Long> itemIdList = itemStorage.findAllByOwnerId(userId).stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> commentList = commentRepository.getCommentsByItemIdIn(itemIdList);
        List<Booking> bookings = bookingRepository.getBookingsByItemIdInOrderByStartDateAsc(itemIdList);
        Map<Long, List<Comment>> commentsForCurrentItem = new HashMap<>();
        Map<Long, List<Booking>> bookingsForCurrentItem = new HashMap<>();
        itemIdList.forEach(i -> {
            commentsForCurrentItem.put(i, commentList.stream().filter(c -> c.getItemId().equals(i)).collect(Collectors.toList()));
            bookingsForCurrentItem.put(i, bookings.stream().filter(b -> b.getItem().getId().equals(i)).collect(Collectors.toList()));
        });
        itemWithCommentDtoList.forEach(i -> {
            if (commentsForCurrentItem.containsKey(i.getId())) {
                i.setComments(CommentMapper.toCommentDtoList(commentsForCurrentItem.get(i.getId())));
            }
            i.setNextBooking(BookingMapper.toBookingSimplyDto(bookingsForCurrentItem.get(i.getId()).isEmpty() ? null
                    : bookingsForCurrentItem.get(i.getId()).get(1)));
            i.setLastBooking(BookingMapper.toBookingSimplyDto(bookingsForCurrentItem.get(i.getId()).isEmpty() ? null
                    : bookingsForCurrentItem.get(i.getId()).get(0)));
        });
        return itemWithCommentDtoList;
    }

    public ItemWithCommentDto getItem(Long itemId, Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("This user was not found"));
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("This item was not found"));
        ItemWithCommentDto itemWithCommentDto = ItemMapper.toItemWithCommentDto(item);
        if (commentRepository.existsByItemId(itemId)) {
            itemWithCommentDto.setComments(CommentMapper.toCommentDtoList(commentRepository.getCommentsByItemId(itemId)));
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            if (!(bookingRepository.findAllByStartDateIsAfterAndItemIdAndStatusOrderByStartDateAsc(LocalDateTime.now(),
                    itemWithCommentDto.getId(), BookingStatus.APPROVED)).isEmpty()) {
                itemWithCommentDto.setNextBooking(BookingMapper.toBookingSimplyDto(bookingRepository.findAllByStartDateIsAfterAndItemIdAndStatusOrderByStartDateAsc(LocalDateTime.now(),
                        itemWithCommentDto.getId(), BookingStatus.APPROVED).get(0)));
            }

            if (!(bookingRepository.findAllByStartDateIsBeforeAndItemIdAndStatusOrderByStartDateDesc(LocalDateTime.now(),
                    itemWithCommentDto.getId(), BookingStatus.APPROVED)).isEmpty()) {
                itemWithCommentDto.setLastBooking(BookingMapper.toBookingSimplyDto(bookingRepository.findAllByStartDateIsBeforeAndItemIdAndStatusOrderByStartDateDesc(LocalDateTime.now(),
                        itemWithCommentDto.getId(), BookingStatus.APPROVED).get(0)));
            }
        }
        return itemWithCommentDto;
    }

    @Transactional
    public ItemDto addItem(Long userId, ItemDto item) {
        User owner = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("This user was not found"));

        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(owner);
        if (item.getRequestId() != null) {
            newItem.setRequest(itemRequestRepository.findById(item.getRequestId()).orElseThrow(() -> new NotFoundException("This request was not found")));
        }
        return ItemMapper.toItemDto(itemStorage.save(newItem));
    }

    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("This user was not found");
        }
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException("This item was not found");
        }
        Item savedItem = itemStorage.findByIdAndOwnerId(itemId, userId);
        if (savedItem == null) {
            throw new NotFoundException("Item for this user was not found");
        }
        Item updateItem = Item.builder()
                .id(item.getId() != null ? item.getId() : savedItem.getId())
                .name(item.getName() != null ? item.getName() : savedItem.getName())
                .available(item.getAvailable() != null ? item.getAvailable() : savedItem.getAvailable())
                .description(item.getDescription() != null ? item.getDescription() : savedItem.getDescription())
                .owner(savedItem.getOwner())
                .build();
        return ItemMapper.toItemDto(itemStorage.save(updateItem));
    }

    @Transactional
    public Boolean deleteItem(Long itemId) {
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException("This item was not found");
        }
        itemStorage.deleteById(itemId);
        return !itemStorage.existsById(itemId);
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemStorage.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text));
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("This user was not found"));
        itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("This item was not found"));

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndDateIsBeforeOrderByStartDateDesc(userId, LocalDateTime.now());
        if (bookings.stream().anyMatch(b -> Objects.equals(b.getItem().getId(), itemId)
                && Objects.equals(b.getBooker().getId(), userId))) {
            commentDto.setAuthorName(user.getName());
            commentDto.setItemId(itemId);
            commentDto.setCreated(LocalDateTime.now());

            return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, user)));
        } else {
            throw new InvalidArgumentsException("This user never rent this item");
        }
    }
}
