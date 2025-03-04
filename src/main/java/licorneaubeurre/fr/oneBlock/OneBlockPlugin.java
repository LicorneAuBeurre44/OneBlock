package licorneaubeurre.fr.oneBlock;

import org.bukkit.plugin.java.JavaPlugin;

public class OneBlockPlugin extends JavaPlugin {

    private IslandManager islandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Crée config.yml s'il n'existe pas

        // Passer le fichier de configuration à l'IslandManager
        islandManager = new IslandManager(getConfig());
        getCommand("oneblock").setExecutor(new OneBlockCommand(islandManager));
        getServer().getPluginManager().registerEvents(new OneBlockListener(islandManager, this), this);
    }
}
