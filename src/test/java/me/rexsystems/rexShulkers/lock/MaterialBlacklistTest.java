package me.rexsystems.rexShulkers.lock;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialBlacklistTest {

    @Test
    void listedMaterialIsBlocked() {
        MaterialBlacklist blacklist = new MaterialBlacklist(Arrays.asList("bedrock", "BARRIER", " ", null));

        assertTrue(blacklist.blocked("BEDROCK"));
        assertTrue(blacklist.blocked("barrier"));
        assertFalse(blacklist.blocked("DIAMOND"));
        assertFalse(blacklist.blocked(""));
        assertFalse(blacklist.blocked(null));
    }

    @Test
    void clicksOutsideTheShulkerGuiAreNotCancelled() {
        MaterialBlacklist blacklist = new MaterialBlacklist(List.of("BEDROCK"));

        assertFalse(ClickGuard.cancel(false, blacklist.blocked("BEDROCK"), false));
        assertTrue(ClickGuard.cancel(true, blacklist.blocked("BEDROCK"), false));
        assertFalse(ClickGuard.cancel(true, blacklist.blocked("DIAMOND"), false));
        assertTrue(ClickGuard.cancel(false, false, true));
    }
}
