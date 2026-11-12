package me.rexsystems.rexShulkers.item;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ShulkerTagTest {

    @Test
    void clearRemovesTheId() {
        UUID id = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        ShulkerTag tag = new ShulkerTag(id);

        assertEquals(id, tag.id());
        assertEquals(id, tag.clear());
        assertNull(tag.id());
        assertFalse(tag.hasId());
        assertNull(tag.clear());
    }
}
