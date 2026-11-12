package me.rexsystems.rexShulkers.session;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class OpenSession {

    private final UUID storageId;
    private final UUID playerId;
    private final Inventory inventory;
    private final AtomicBoolean saved = new AtomicBoolean();
    private volatile ItemStack[] latest = new ItemStack[0];

    public OpenSession(UUID storageId, UUID playerId, Inventory inventory) {
        this.storageId = storageId;
        this.playerId = playerId;
        this.inventory = inventory;
    }

    public UUID storageId() {
        return storageId;
    }

    public UUID playerId() {
        return playerId;
    }

    public Inventory inventory() {
        return inventory;
    }

    public boolean markSaved() {
        return saved.compareAndSet(false, true);
    }

    public void update(ItemStack[] contents) {
        this.latest = contents == null ? new ItemStack[0] : contents;
    }

    public ItemStack[] latest() {
        ItemStack[] current = latest;
        return current == null ? new ItemStack[0] : current;
    }
}
