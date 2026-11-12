package me.rexsystems.rexShulkers.lock;

public final class ClickGuard {

    private ClickGuard() {
    }

    public static boolean cancel(boolean shulkerGuiInsert, boolean blacklistedMaterial, boolean lockedShulkerItem) {
        if (lockedShulkerItem) {
            return true;
        }
        return shulkerGuiInsert && blacklistedMaterial;
    }
}
