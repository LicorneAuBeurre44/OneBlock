package licorneaubeurre.fr.oneBlock;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.world.level.border.WorldBorder;

import java.lang.reflect.Field;

public class WorldBorderUtils {
    public static void sendWorldBorder(Player player, Location center, double radius) {
        WorldBorder border = new WorldBorder();
        // On récupère l'instance NMS du monde via CraftWorld (v1_20_R3)
        try {
            Object nmsWorld = ((CraftWorld) center.getWorld()).getHandle();
            Field worldField = WorldBorder.class.getDeclaredField("world");
            worldField.setAccessible(true);
            worldField.set(border, nmsWorld);
        } catch (Exception e) {
            e.printStackTrace();
        }

        border.setCenter(center.getX(), center.getZ());
        border.setSize(radius * 2); // taille = diamètre
        border.setWarningBlocks((int)(radius / 10));

        ClientboundInitializeBorderPacket packet = new ClientboundInitializeBorderPacket(border);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}
