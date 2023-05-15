package ru.practicum.shareit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserHeaderTest {
    @Test
    public void shouldBeFinal() {
        Assertions.assertNotNull(UserHeader.OWNER_ID);
        Assertions.assertEquals(UserHeader.OWNER_ID, "X-Sharer-User-Id");
    }

}