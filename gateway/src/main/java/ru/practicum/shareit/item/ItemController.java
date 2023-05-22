package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@Controller
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create item by userId={}", userId);
        return itemClient.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") Long itemId,
                                             @RequestBody ItemToUpdateDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Update item by itemId={}", itemId);
        return itemClient.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findByItemId(@PathVariable("itemId") Long itemId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find item by itemId={}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items by userId={}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Search items by text '{}'", text);
        return text.isBlank() ? ResponseEntity.ok(new ArrayList<>()) : itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        log.info("Add comment by userId={}", userId);
        return itemClient.addComment(commentDto, userId, itemId);
    }
}
