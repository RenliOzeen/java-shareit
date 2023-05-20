package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getCommentsByItemId(Long itemId);

    boolean existsByItemId(Long itemId);

    List<Comment> getCommentsByItemIdIn(List<Long> itemIds);
}
