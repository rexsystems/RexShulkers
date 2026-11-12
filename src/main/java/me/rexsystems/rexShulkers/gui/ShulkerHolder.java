package me.rexsystems.rexShulkers.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public final class ShulkerHolder implements InventoryHolder {

    private final UUID storageId;
    private final UUID playerId;
    private Inventory inventory;

    public ShulkerHolder(UUID storageId, UUID playerId) {
        this.storageId = storageId;
        this.playerId = playerId;
    }

    public void attach(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public UUID storageId() {
        return storageId;
    }

    public UUID playerId() {
        return playerId;
    }
}
