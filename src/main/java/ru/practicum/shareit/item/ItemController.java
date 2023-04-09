package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
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
    public List<Item> getAllItems(@RequestHeader(USERID_HEADER) Long userId) {
        log.info("Получен запрос на получение списка всех вещей");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Long itemId) {
        log.info("Получен запрос на получение вещи по ее id");
        return itemService.getItem(itemId);
    }

    @PostMapping
    public Item addItem(@RequestHeader(USERID_HEADER) Long userId, @Valid @RequestBody Item item) {
        log.info("Получен запрос на создание вещи");
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(USERID_HEADER) Long userId,
                           @PathVariable Long itemId,
                           @RequestBody Item item) {
        log.info("Получен запрос на обновление вещи");
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public Boolean deleteItem(@PathVariable Long itemId) {
        log.info("Получен запрос на удаление вещи");
        return itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи по части имени или описания");
        return itemService.searchItems(text);
    }
}
