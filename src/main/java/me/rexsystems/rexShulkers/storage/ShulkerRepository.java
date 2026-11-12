package me.rexsystems.rexShulkers.storage;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class ShulkerRepository {

    private final File file;
    private final Logger logger;
    private final Object monitor = new Object();
    private final ConcurrentHashMap<UUID, ItemStack[]> pending = new ConcurrentHashMap<>();
    private Connection connection;

    public ShulkerRepository(File file, Logger logger) {
        this.file = file;
        this.logger = logger;
    }

    public void open() throws SQLException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new SQLException("Could not create " + parent.getAbsolutePath());
        }
        loadDriver();
        synchronized (monitor) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA journal_mode=WAL");
                statement.execute("PRAGMA busy_timeout=5000");
                statement.execute("PRAGMA synchronous=NORMAL");
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS shulkers (id TEXT PRIMARY KEY, contents BLOB NOT NULL)");
            }
        }
    }

    public ItemStack[] load(UUID id) {
        synchronized (monitor) {
            ItemStack[] held = pending.get(id);
            if (held != null) {
                return ContentCodec.copy(held);
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT contents FROM shulkers WHERE id = ?")) {
                statement.setString(1, id.toString());
                try (ResultSet rows = statement.executeQuery()) {
                    if (!rows.next()) {
                        return new ItemStack[0];
                    }
                    return ContentCodec.decode(rows.getBytes("contents"));
                }
            } catch (SQLException | IOException ex) {
                throw new IllegalStateException("Could not load shulker " + id, ex);
            }
        }
    }

    public void save(UUID id, ItemStack[] contents) {
        ItemStack[] copy = ContentCodec.copy(contents);
        synchronized (monitor) {
            pending.put(id, copy);
            try {
                byte[] blob = ContentCodec.encode(copy);
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO shulkers (id, contents) VALUES (?, ?) "
                                + "ON CONFLICT(id) DO UPDATE SET contents = excluded.contents")) {
                    statement.setString(1, id.toString());
                    statement.setBytes(2, blob);
                    statement.executeUpdate();
                }
                pending.remove(id, copy);
            } catch (SQLException | IOException ex) {
                throw new IllegalStateException("Could not save shulker " + id, ex);
            }
        }
    }

    public void delete(UUID id) {
        synchronized (monitor) {
            pending.remove(id);
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM shulkers WHERE id = ?")) {
                statement.setString(1, id.toString());
                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new IllegalStateException("Could not delete shulker " + id, ex);
            }
        }
    }

    public void close() {
        synchronized (monitor) {
            if (connection == null) {
                return;
            }
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.warning("Could not close shulkers.db: " + ex.getMessage());
            }
            connection = null;
        }
    }

    private static void loadDriver() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException first) {
            try {
                Class.forName("me.rexsystems.rexShulkers.libs.sqlite.JDBC");
            } catch (ClassNotFoundException second) {
                SQLException failure = new SQLException("sqlite-jdbc is not on the classpath");
                failure.addSuppressed(first);
                failure.addSuppressed(second);
                throw failure;
            }
        }
    }
}
