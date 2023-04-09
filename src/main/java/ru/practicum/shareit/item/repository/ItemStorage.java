package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    /**
     * Метод для получения списка всех вещей по определенному пользователю
     *
     * @param userId идентификатор пользователя
     * @return список экземпляров Item
     */
    List<Item> getAllItems(Long userId);

    /**
     * Метод для получения вещи по ее идентификатору
     *
     * @param itemId идентификатор вещи
     * @return экземпляр Item
     */
    Item getItem(Long itemId);

    /**
     * Метод для создания вещи
     *
     * @param userId идентификатор пользователя
     * @param item   экземпляр Item
     * @return созданный экземпляр Item
     */
    Item addItem(Long userId, Item item);

    /**
     * Метод для обновления данных пользователя
     *
     * @param userId идентификатор пользователя
     * @param itemId идентификатор вещи в системе, которую нужно обновить
     * @param item   экземпляр Item, содержащий данные для обновления
     * @return обновленный экземпляр Item
     */
    Item updateItem(Long userId, Long itemId, Item item);

    /**
     * Метод для удаления вещи по ее идентификатору
     *
     * @param itemId идентификатор вещи
     * @return Boolean результат удаления
     */
    Boolean deleteItem(Long itemId);

    /**
     * Метод для поиска вещей, доступных для аренды, по части названия или описания
     *
     * @param text часть названия или описания, по которой проводится поиск
     * @return список найденных экземпляров Item
     */
    List<Item> searchItems(String text);
}
