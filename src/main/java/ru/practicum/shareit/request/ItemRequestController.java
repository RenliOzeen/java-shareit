package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(
            @RequestHeader(USERID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsForCurrentUser(@RequestHeader(USERID_HEADER) Long userId) {
        return itemRequestService.getRequestsForCurrentUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(USERID_HEADER) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequest(userId, requestId);
    }


    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(USERID_HEADER) Long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

}
