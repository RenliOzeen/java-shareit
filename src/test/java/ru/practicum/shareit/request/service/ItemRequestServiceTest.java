package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.InvalidArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemRequestService itemRequestService;
    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        ItemDto itemDto = ItemDto.builder()
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

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestOwnerId(2L)
                .description("description")
                .created(LocalDateTime.now())
                .items(List.of(itemDto))
                .build();
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
    }

    @Test
    void shouldGetAllRequestOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemRequestService.findAllRequests(2L, 0, 10));
        assertThrows(InvalidArgumentsException.class, () -> itemRequestService.findAllRequests(1L, -1, -2));

        itemRequest.setRequestor(user);
        itemRequest.getRequestor().setId(2L);
        Mockito.when(itemRequestRepository.getItemRequestsByRequestorIsNot(Mockito.any(User.class), Mockito.any()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));
        List<ItemRequestDto> result = itemRequestService.findAllRequests(1L, 0, 10);
        assertFalse(result.isEmpty());
        assertNotNull(result.get(0));
    }

    @Test
    void shouldGetRequestForCurrentUserOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsForCurrentUser(2L));
        Mockito.when(itemRequestRepository.getItemRequestsByRequestorOrderByCreateDateDesc(user))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        itemRequest.setRequestor(user);
        List<ItemRequestDto> result = itemRequestService.getRequestsForCurrentUser(user.getId());
        assertFalse(result.isEmpty());
        assertNotNull(result.get(0));
    }

    @Test
    void shouldGetRequestOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequest(2L, 1L));
        Mockito.when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequest(1L, 2L));

        itemRequest.setRequestor(user);
        Mockito.when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));
        ItemRequestDto result = itemRequestService.getRequest(1L, 1L);
        assertNotNull(result);
    }

    @Test
    void shouldAddRequestOrThrowException() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(2L, itemRequestDto));

        itemRequest.setRequestor(user);
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.addRequest(1L, itemRequestDto);
        assertNotNull(result);
    }
}