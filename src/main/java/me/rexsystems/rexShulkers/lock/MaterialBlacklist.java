package me.rexsystems.rexShulkers.lock;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class MaterialBlacklist {

    private final Set<String> names;

    public MaterialBlacklist(Collection<String> materials) {
        Set<String> parsed = new HashSet<>();
        if (materials != null) {
            for (String material : materials) {
                if (material == null || material.isBlank()) {
                    continue;
                }
                parsed.add(material.trim().toUpperCase(Locale.ROOT));
            }
        }
        this.names = Set.copyOf(parsed);
    }

    public boolean blocked(String material) {
        if (material == null || material.isBlank()) {
            return false;
        }
        return names.contains(material.trim().toUpperCase(Locale.ROOT));
    }
}
