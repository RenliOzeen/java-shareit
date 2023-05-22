package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String OWNER_ID = "X-Sharer-User-Id";

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(
            @RequestHeader(OWNER_ID) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsForCurrentUser(@RequestHeader(OWNER_ID) Long userId) {
        return itemRequestService.getRequestsForCurrentUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(OWNER_ID) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequest(userId, requestId);
    }


    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(OWNER_ID) Long userId,
                                     @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

}
