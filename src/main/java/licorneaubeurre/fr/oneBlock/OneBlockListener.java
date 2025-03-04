package licorneaubeurre.fr.oneBlock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.UUID;

public class OneBlockListener implements Listener {

    private final IslandManager islandManager;
    private final JavaPlugin plugin;
    private final Random random;

    public OneBlockListener(IslandManager islandManager, JavaPlugin plugin) {
        this.islandManager = islandManager;
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        UUID owner = islandManager.getIslandOwner(loc);

        // Si le bloc ne correspond pas à une île, ne rien faire
        if (owner == null) return;

        Player player = event.getPlayer();
        // Seul le propriétaire peut casser son OneBlock
        if (!player.getUniqueId().equals(owner)) {
            player.sendMessage("§cVous ne pouvez pas casser l'île d'un autre joueur !");
            event.setCancelled(true);
            return;
        }

        // On laisse le drop par défaut (ou vous pouvez customiser)
        event.setDropItems(true);

        // Régénérer le OneBlock après 1 tick avec un nouveau bloc aléatoire, pas 1 ticK
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            block.setType(getRandomBlock());
        }, 1L);
    }

    private Material getRandomBlock() {
        Material[] blocks = new Material[] {
                Material.DIAMOND_ORE,
                Material.GOLD_ORE,
                Material.IRON_ORE,
                Material.EMERALD_ORE,
                Material.COAL_ORE,
                Material.LAPIS_ORE,
                Material.REDSTONE_ORE
        };
        return blocks[random.nextInt(blocks.length)];
    }
}
