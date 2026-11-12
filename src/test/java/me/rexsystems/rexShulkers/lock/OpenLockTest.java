package me.rexsystems.rexShulkers.lock;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenLockTest {

    @Test
    void secondStackWithTheSameIdIsRejected() {
        OpenLock lock = new OpenLock();
        UUID storageId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID firstStack = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID secondStack = UUID.fromString("33333333-3333-3333-3333-333333333333");

        assertTrue(lock.tryOpen(storageId, firstStack));
        assertFalse(lock.tryOpen(storageId, secondStack));
        assertTrue(lock.isOpen(storageId));
        assertEquals(firstStack, lock.player(storageId));

        lock.release(storageId);
        assertFalse(lock.isOpen(storageId));
        assertTrue(lock.tryOpen(storageId, secondStack));
    }
}
