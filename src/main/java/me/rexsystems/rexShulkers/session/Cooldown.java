package me.rexsystems.rexShulkers.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Cooldown {

    private final ConcurrentHashMap<UUID, Long> closedAt = new ConcurrentHashMap<>();

    public void mark(UUID playerId, long nowMillis) {
        if (playerId == null) {
            return;
        }
        closedAt.put(playerId, nowMillis);
    }

    public long remainingMillis(UUID playerId, long cooldownMillis, long nowMillis) {
        if (playerId == null || cooldownMillis <= 0) {
            return 0L;
        }
        Long closed = closedAt.get(playerId);
        if (closed == null) {
            return 0L;
        }
        long left = cooldownMillis - (nowMillis - closed);
        return Math.max(0L, left);
    }
}
