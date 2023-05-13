package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void shouldAddRequest() throws Exception {
        Mockito
                .when(itemRequestService.addRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnRequestForCurrentUser() throws Exception {
        Mockito
                .when(itemRequestService.getRequestsForCurrentUser(anyLong()))
                .thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnRequest() throws Exception {
        Mockito
                .when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAllRequests() throws Exception {
        Mockito
                .when(itemRequestService.findAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L))
                .andExpect(status().isOk());
    }

}