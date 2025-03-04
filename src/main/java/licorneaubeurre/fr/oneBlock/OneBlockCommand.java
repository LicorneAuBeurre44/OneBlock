package licorneaubeurre.fr.oneBlock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;

public class OneBlockCommand implements CommandExecutor {

    private final IslandManager islandManager;

    public OneBlockCommand(IslandManager islandManager) {
        this.islandManager = islandManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            // Si l'argument "item" est présent, donner l'item custom au joueur
            if (args.length > 0 && args[0].equalsIgnoreCase("item")) {
                player.getInventory().addItem(getCustomBlockItem());
                player.sendMessage(ChatColor.GREEN + "Vous avez reçu le bloc OneBlock custom !");
                return true;
            }

            Location islandLocation;
            if (!islandManager.hasIsland(player)) {
                islandManager.createIsland(player);
                islandLocation = islandManager.getIslandLocation(player);
                player.sendMessage(ChatColor.GREEN + "Votre île OneBlock a été créée !");
            } else {
                islandLocation = islandManager.getIslandLocation(player);
                player.sendMessage(ChatColor.RED + "Vous avez déjà une île. Téléportation en cours...");
                player.teleport(islandLocation);
            }
            // Envoi de la world border personnalisée centrée sur l'île
            WorldBorderUtils.sendWorldBorder(player, islandLocation, islandManager.getWorldBorderSize());
            return true;
        }
        return false;
    }

    // Méthode utilitaire pour créer l'item custom OneBlock
    private ItemStack getCustomBlockItem() {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "OneBlock Custom");
        meta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Un bloc spécial ayant les mêmes",
                ChatColor.GRAY + "propriétés que votre île OneBlock."));
        item.setItemMeta(meta);
        return item;
    }
}
