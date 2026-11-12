package me.rexsystems.rexShulkers.item;

import java.util.UUID;

public final class ShulkerTag {

    private UUID id;

    public ShulkerTag(UUID id) {
        this.id = id;
    }

    public static UUID parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public UUID id() {
        return id;
    }

    public boolean hasId() {
        return id != null;
    }

    public UUID clear() {
        UUID removed = id;
        id = null;
        return removed;
    }
}
