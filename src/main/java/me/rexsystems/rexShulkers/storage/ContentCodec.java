package me.rexsystems.rexShulkers.storage;

import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ContentCodec {

    private static final int VERSION = 1;

    private ContentCodec() {
    }

    public static ItemStack[] copy(ItemStack[] contents) {
        if (contents == null) {
            return new ItemStack[0];
        }
        ItemStack[] copy = new ItemStack[contents.length];
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack stack = contents[slot];
            copy[slot] = stack == null ? null : stack.clone();
        }
        return copy;
    }

    public static byte[] encode(ItemStack[] contents) throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeInt(VERSION);
            int size = contents == null ? 0 : contents.length;
            out.writeInt(size);
            for (int slot = 0; slot < size; slot++) {
                ItemStack stack = contents[slot];
                if (stack == null || stack.getType().isAir() || stack.getAmount() <= 0) {
                    out.writeInt(-1);
                    continue;
                }
                byte[] data = stack.serializeAsBytes();
                out.writeInt(data.length);
                out.write(data);
            }
        }
        return raw.toByteArray();
    }

    public static ItemStack[] decode(byte[] blob) throws IOException {
        if (blob == null || blob.length == 0) {
            return new ItemStack[0];
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(blob))) {
            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException("Unknown shulker contents version " + version);
            }
            int size = in.readInt();
            if (size < 0 || size > 54) {
                throw new IOException("Unexpected shulker size " + size);
            }
            ItemStack[] contents = new ItemStack[size];
            for (int slot = 0; slot < size; slot++) {
                int length = in.readInt();
                if (length < 0) {
                    continue;
                }
                if (length > 1_000_000) {
                    throw new IOException("Shulker slot is too large");
                }
                byte[] data = in.readNBytes(length);
                if (data.length != length) {
                    throw new IOException("Shulker slot was truncated");
                }
                contents[slot] = ItemStack.deserializeBytes(data);
            }
            return contents;
        }
    }
}
