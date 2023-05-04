package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithCommentDto> getAllItems(@RequestHeader(USERID_HEADER) Long userId) {
        log.info("Получен запрос на получение списка всех вещей");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentDto getItem(@PathVariable Long itemId, @RequestHeader(USERID_HEADER) Long userId) {
        log.info("Получен запрос на получение вещи по ее id");
        return itemService.getItem(itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USERID_HEADER) Long userId, @Valid @RequestBody ItemDto item) {
        log.info("Получен запрос на создание вещи");
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USERID_HEADER) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        log.info("Получен запрос на обновление вещи");
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public Boolean deleteItem(@PathVariable Long itemId) {
        log.info("Получен запрос на удаление вещи");
        return itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи по части имени или описания");
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USERID_HEADER) Long userId, @PathVariable Long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария");
        return itemService.addComment(userId, itemId, commentDto);
    }
}
