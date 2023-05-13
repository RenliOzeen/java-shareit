package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InvalidArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    public List<ItemRequestDto> findAllRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("This user was not found"));
        if (from < 0 || size <= 0) {
            throw new InvalidArgumentsException("'from' and 'size' should be positive");
        }
        List<ItemRequestDto> requestsToReturn = ItemRequestMapper.toItemRequestDtoList(itemRequestRepository.getItemRequestsByRequestorIsNot(user,
                PageRequest.of((from / size), size, Sort.by("createDate").descending())));
        requestsToReturn.forEach(r -> r.setItems(ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(r.getId()))));
        return requestsToReturn.stream().filter(r -> !Objects.equals(r.getRequestOwnerId(), userId))
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getRequestsForCurrentUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("This user was not found"));
        List<ItemRequestDto> requestsToReturn = ItemRequestMapper.toItemRequestDtoList(itemRequestRepository.getItemRequestsByRequestorOrderByCreateDateDesc(user));
        requestsToReturn.forEach(r -> r.setItems(ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(r.getId()))));
        return requestsToReturn;
    }

    public ItemRequestDto getRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("This user was not found"));
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("This request was not found"));
        ItemRequestDto requestToReturn = ItemRequestMapper.toItemRequestDto(request);
        requestToReturn.setItems(ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(requestId)));
        return requestToReturn;
    }

    @Transactional
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("This user was not found"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreateDate(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }
}
