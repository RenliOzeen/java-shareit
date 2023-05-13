package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static final String USERID_HEADER = "X-Sharer-User-Id";

    private ItemDto itemDtoInput;
    private ItemDto itemDtoOutput1;
    private ItemDto itemDtoOutput2;
    private ItemWithCommentDto itemWithCommentDto1;
    private ItemWithCommentDto itemWithCommentDto2;
    private CommentDto newCommentDto;
    private CommentDto savedCommentDto;

    @BeforeEach
    void setup() {

        itemDtoInput = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemDtoOutput1 = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemWithCommentDto1 = ItemWithCommentDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        itemDtoOutput2 = ItemDto.builder()
                .id(2L)
                .name("name1")
                .description("description1")
                .available(true)
                .build();

        itemWithCommentDto2 = ItemWithCommentDto.builder()
                .id(2L)
                .name("name1")
                .description("description1")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        newCommentDto = CommentDto.builder()
                .text("text")
                .build();

        savedCommentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .build();
    }

    @Test
    public void shouldReturnAllItems() throws Exception {
        List<ItemWithCommentDto> outputItems = List.of(itemWithCommentDto1, itemWithCommentDto2);

        Mockito
                .when(itemService.getAllItems(1L))
                .thenReturn(outputItems);

        mockMvc.perform(get("/items")
                        .header(USERID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].comments", is(Collections.emptyList())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("name1")))
                .andExpect(jsonPath("$[1].description", is("description1")))
                .andExpect(jsonPath("$[1].available", is(true)))
                .andExpect(jsonPath("$[1].comments", is(Collections.emptyList())));
    }

    @Test
    public void shouldReturnItem() throws Exception {

        Mockito
                .when(itemService.getItem(1L, 1L))
                .thenReturn(itemWithCommentDto1);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(USERID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.comments", is(Collections.emptyList())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void shouldAddItem() throws Exception {
        Mockito
                .when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDtoOutput1);

        mockMvc.perform(post("/items")
                        .header(USERID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        ItemDto itemDtoForUpdate = ItemDto.builder()
                .name("update")
                .description("update")
                .build();

        ItemDto updatedItemDto = itemDtoOutput1;
        updatedItemDto.setName(itemDtoForUpdate.getName());
        updatedItemDto.setDescription(itemDtoForUpdate.getDescription());

        Mockito
                .when(itemService.updateItem(1L, 1L, itemDtoForUpdate))
                .thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(USERID_HEADER, 1L)
                        .content(asJsonString(itemDtoForUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("update")))
                .andExpect(jsonPath("$.description", is("update")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void shouldSearchItems() throws Exception {
        List<ItemDto> outputItems = List.of(itemDtoOutput1, itemDtoOutput2);

        Mockito
                .when(itemService.searchItems("description"))
                .thenReturn(outputItems);

        mockMvc.perform(get("/items/search")
                        .param("text", "description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("name1")))
                .andExpect(jsonPath("$[1].description", is("description1")))
                .andExpect(jsonPath("$[1].available", is(true)));
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items" + "/1"))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .deleteItem(anyLong());
    }

    @Test
    public void shouldAddComment() throws Exception {
        Mockito
                .when(itemService.addComment(1L, 1L, newCommentDto))
                .thenReturn(savedCommentDto);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .header(USERID_HEADER, 1L)
                        .content(asJsonString(newCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("text")))
                .andExpect(jsonPath("$.authorName", is("author")));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
