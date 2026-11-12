package me.rexsystems.rexShulkers.lock;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class OpenLock {

    private final ConcurrentHashMap<UUID, UUID> openByStorage = new ConcurrentHashMap<>();

    public boolean tryOpen(UUID storageId, UUID playerId) {
        if (storageId == null || playerId == null) {
            return false;
        }
        return openByStorage.putIfAbsent(storageId, playerId) == null;
    }

    public void release(UUID storageId) {
        if (storageId == null) {
            return;
        }
        openByStorage.remove(storageId);
    }

    public boolean isOpen(UUID storageId) {
        return storageId != null && openByStorage.containsKey(storageId);
    }

    public UUID player(UUID storageId) {
        if (storageId == null) {
            return null;
        }
        return openByStorage.get(storageId);
    }
}
