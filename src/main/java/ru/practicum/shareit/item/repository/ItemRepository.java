package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name, String description);

    Item findByIdAndOwnerId(Long itemId, Long ownerId);

    List<Item> findAllByRequestId(Long id);

    List<Item> findAllByRequestIdIn(List<Long> requestIdList);
}
