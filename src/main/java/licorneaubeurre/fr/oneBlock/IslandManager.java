package licorneaubeurre.fr.oneBlock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IslandManager {
    private final HashMap<UUID, Location> islands = new HashMap<>();
    private World world;
    private final int islandSpacing;
    private final int worldBorderSize;
    private Connection connection;
    private final FileConfiguration config;

    public IslandManager(FileConfiguration config) {
        this.config = config;
        this.islandSpacing = config.getInt("island-spacing", 100);
        this.worldBorderSize = config.getInt("worldborder-size", 50);
        setupDatabase();

        // Création ou chargement du monde dédié aux îles OneBlock
        if (Bukkit.getWorld("oneblock_world") == null) {
            WorldCreator worldCreator = new WorldCreator("oneblock_world");
            worldCreator.environment(World.Environment.NORMAL);
            worldCreator.type(WorldType.FLAT);
            worldCreator.generateStructures(false);
            this.world = worldCreator.createWorld();
        } else {
            this.world = Bukkit.getWorld("oneblock_world");
        }

        loadIslandsFromDatabase();
    }

    private void setupDatabase() {
        try {
            String dbType = config.getString("database.type", "sqlite");
            if (dbType.equalsIgnoreCase("mysql")) {
                String host = config.getString("database.host", "localhost");
                int port = config.getInt("database.port", 3306);
                String database = config.getString("database.database", "oneblock");
                String username = config.getString("database.username", "root");
                String password = config.getString("database.password", "");
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
                connection = DriverManager.getConnection(url, username, password);
            } else {
                connection = DriverManager.getConnection("jdbc:sqlite:plugins/OneBlock/islands.db");
            }
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS islands (uuid TEXT PRIMARY KEY, x INTEGER, y INTEGER, z INTEGER)");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadIslandsFromDatabase() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM islands");
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");
                islands.put(uuid, new Location(world, x, y, z));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasIsland(Player player) {
        return islands.containsKey(player.getUniqueId());
    }

    public void createIsland(Player player) {
        int offset = islands.size() * islandSpacing;
        Location islandLocation = new Location(world, offset, 100, 0);
        islands.put(player.getUniqueId(), islandLocation);
        world.getBlockAt(islandLocation).setType(Material.GRASS_BLOCK);
        saveIslandToDatabase(player.getUniqueId(), islandLocation);
    }

    private void saveIslandToDatabase(UUID uuid, Location location) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO islands (uuid, x, y, z) VALUES (?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location getIslandLocation(Player player) {
        return islands.get(player.getUniqueId());
    }

    public UUID getIslandOwner(Location location) {
        for (Map.Entry<UUID, Location> entry : islands.entrySet()) {
            if (entry.getValue().equals(location)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public int getWorldBorderSize() {
        return worldBorderSize;
    }
}
