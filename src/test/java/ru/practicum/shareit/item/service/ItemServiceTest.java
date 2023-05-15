package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingSimplyDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemService itemService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private User user;

    private Booking booking;

    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .requestId(1L)
                .description("item description")
                .available(true)
                .build();
        item = ItemMapper.toItem(itemDto);

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();
        user = UserMapper.toUser(userDto);

        BookingSimplyDto bookingSimplyDto = BookingSimplyDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-06-12 00:00", formatter))
                .end(LocalDateTime.parse("2023-07-12 00:00", formatter))
                .itemId(item.getId())
                .bookerId(user.getId())
                .build();
        booking = BookingMapper.toBooking(bookingSimplyDto, item, user);

        commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .itemId(item.getId())
                .authorName(user.getName())
                .created(LocalDateTime.now())
                .build();
        comment = CommentMapper.toComment(commentDto, user);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestOwnerId(2L)
                .description("description")
                .created(LocalDateTime.now())
                .items(List.of(itemDto))
                .build();
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
    }

    @Test
    void shouldGetAllItemsOrThrowException() {
        Booking booking1 = booking;
        booking1.setId(2L);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemService.getAllItems(2L));

        Mockito.when(commentRepository.existsByItemId(item.getId())).thenReturn(true);
        Mockito.when(itemRepository.findAllByOwnerId(user.getId())).thenReturn(List.of(item));
        Mockito.when(commentRepository.getCommentsByItemIdIn(List.of(item.getId()))).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.getBookingsByItemIdInOrderByStartDateAsc(List.of(item.getId()))).thenReturn(List.of(booking,booking1));

        List<ItemWithCommentDto> result = itemService.getAllItems(user.getId());

        assertNotNull(result);
        assertEquals(result.get(0).getId(), item.getId());
        assertFalse(result.get(0).getComments().isEmpty());
    }

    @Test
    void shouldGetItemOrThrowException() {
        item.setOwner(user);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 2L));

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.getItem(2L, 1L));

        Mockito.when(commentRepository.existsByItemId(item.getId())).thenReturn(true);
        Mockito.when(commentRepository.getCommentsByItemId(item.getId())).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findAllByStartDateIsAfterAndItemIdAndStatusOrderByStartDateAsc(Mockito.any(),
                Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findAllByStartDateIsBeforeAndItemIdAndStatusOrderByStartDateDesc(Mockito.any(),
                Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

        ItemWithCommentDto result = itemService.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals(result.getNextBooking().getId(), booking.getId());
        assertEquals(result.getLastBooking().getId(), booking.getId());
        assertFalse(result.getComments().isEmpty());
    }

    @Test
    void shouldAddItemOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemService.addItem(2L, itemDto));
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        itemDto.setRequestId(2L);
        assertThrows(NotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));

        itemDto.setRequestId(1L);
        ItemDto result = itemService.addItem(user.getId(), itemDto);
        assertNotNull(result);
    }

    @Test
    void shouldUpdateItemOrThrowException() {
        itemDto.setName("updated");
        Item updatedItem = ItemMapper.toItem(itemDto);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemService.updateItem(2L, 1L, itemDto));
        Mockito.when(itemRepository.existsById(item.getId())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 2L, itemDto));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, itemDto));
        Mockito.when(itemRepository.findByIdAndOwnerId(item.getId(), user.getId())).thenReturn(item);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, 1L, itemDto);

        assertNotNull(result);
        assertEquals("updated", result.getName());
    }

    @Test
    void shouldThrowExceptionOnDeleteNotExistsItem() {
        Mockito.when(itemRepository.existsById(item.getId())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemService.deleteItem(2L));

        itemService.deleteItem(1L);
        Mockito.verify(itemRepository).deleteById(1L);
    }

    @Test
    void shouldSearchItems() {
        String text = "";
        Mockito.when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text))
                .thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.searchItems(text);
        assertTrue(result.isEmpty());
        text = "item";
        Mockito.when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text))
                .thenReturn(List.of(item));
        result = itemService.searchItems(text);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldAddCommentOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemService.addComment(2L, 1L, commentDto));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 2L, commentDto));

        booking.getItem().setId(2L);
        Mockito.when(bookingRepository.findAllByBookerIdAndEndDateIsBeforeOrderByStartDateDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking));
        assertThrows(InvalidArgumentsException.class, () -> itemService.addComment(1L, 1L, commentDto));

        booking.getItem().setId(1L);

        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentDto result = itemService.addComment(1L, 1L, commentDto);
        assertNotNull(result);
    }
}