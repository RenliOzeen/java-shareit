package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    private CommentDto commentDto;
    private Comment comment;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .itemId(1L)
                .authorName("name")
                .created(LocalDateTime.now())
                .itemId(1L)
                .text("text")
                .build();

        comment = Comment.builder()
                .id(1L)
                .itemId(1L)
                .author(user)
                .created(LocalDateTime.now())
                .itemId(1L)
                .text("text")
                .build();
    }

    @Test
    void shouldReturnCommentDto() {
        CommentDto result = CommentMapper.toCommentDto(comment);
        assertNotNull(result);
        assertEquals(result.getText(), "text");
    }

    @Test
    void shouldReturnComment() {
        Comment result = CommentMapper.toComment(commentDto, user);
        assertNotNull(result);
        assertEquals(result.getText(), "text");
        assertEquals(result.getAuthor(), user);
    }

    @Test
    void shouldReturnCommentDtoList() {
        List<CommentDto> result = CommentMapper.toCommentDtoList(List.of(comment));
        assertNotNull(result.get(0));
        assertEquals(result.get(0).getText(), "text");
        assertEquals(result.get(0).getAuthorName(), user.getName());
    }
}