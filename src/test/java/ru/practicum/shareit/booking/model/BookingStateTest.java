package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingStateTest {

    @ParameterizedTest
    @CsvSource(value = {
            "ALL",
            "CURRENT",
            "PAST",
            "FUTURE",
            "WAITING",
            "REJECTED"
    })
    void shouldReturnCurrentState(String state) {
        assertTrue(Arrays.stream(BookingState.values()).anyMatch(s -> s.equals(BookingState.valueOf(state))));
    }
}