package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.InvalidArgumentsException;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String USERID_HEADER = "X-Sharer-User-Id";
    @MockBean
    private BookingService bookingService;
    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
    private final BookingDto inputBookingDto = BookingDto.builder()
            .start(LocalDateTime.parse("2023-06-12 00:00", formatter))
            .end(LocalDateTime.parse("2023-07-12 00:00", formatter))
            .id(1L).build();
    private final BookingDto bookingDtoWithStartBeforeCurrent = BookingDto.builder()
            .start(LocalDateTime.parse("2022-06-12 00:00", formatter))
            .end(LocalDateTime.parse("2022-06-15 00:00", formatter))
            .id(1L).build();
    private final BookingDto outputBookingDto = BookingDto.builder()
            .start(LocalDateTime.parse("2023-06-12 00:00", formatter))
            .end(LocalDateTime.parse("2023-07-12 00:00", formatter))
            .item(item)
            .build();


    @Test
    void shouldReturnBookingById() throws Exception {
        Mockito
                .when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(outputBookingDto);

        mvc.perform(get("/bookings" + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(outputBookingDto)));
    }

    @Test
    void shouldReturnAllBookingsForBooker() throws Exception {
        Mockito
                .when(bookingService.getAllBookingsForBookerOrItemOwner(any(), any(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(List.of(outputBookingDto));

        mvc.perform(get("/bookings" + "?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(outputBookingDto))));
    }

    @Test
    void shouldAddBooking() throws Exception {
        Mockito
                .when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(outputBookingDto);
        mvc.perform(post("/bookings")
                        .header(USERID_HEADER, 1L)
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(outputBookingDto)));
    }

    @Test
    void shouldChangeBookingStatusApproved() throws Exception {
        Mockito
                .when(bookingService.approveBookingRequest(anyLong(), anyLong()))
                .thenReturn(outputBookingDto);

        mvc.perform(patch("/bookings" + "/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(outputBookingDto)));
    }

    @Test
    public void shouldReturnBadRequestOnBookingStatusChanged() throws Exception {
        Mockito
                .when(bookingService.approveBookingRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new InvalidArgumentsException("Invalid arguments"));

        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .header(USERID_HEADER, 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(outputBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertNotNull(result.getResolvedException()));
    }

    @Test
    void shouldThrowBadRequestOnAddBooking() throws Exception {
        Mockito
                .when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(outputBookingDto);
        mvc.perform(post("/bookings")
                        .header(USERID_HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingDtoWithStartBeforeCurrent))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAllBookingsForItemOwner() throws Exception {
        Mockito
                .when(bookingService.getAllBookingsForBookerOrItemOwner(any(), any(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(List.of(outputBookingDto));

        mvc.perform(get("/bookings" + "/owner?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(outputBookingDto))));
    }

}