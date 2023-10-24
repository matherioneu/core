package eu.matherion.core.shared.commons;

import eu.matherion.core.CoreApplication;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class Bukkits {

    private static CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);

    public static String locationToString(Location location) {
        return String.format("%s;%s;%s;%s;%s;%s", location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static Location stringToLocation(String string) {
        String[] split = string.split(";");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public static void teleportFancy(Player player, int bells, Location location) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, (float) 0.5);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("§6§l! §eTeleportuji za §6%s§e... §6§l!", bells)));

        Bukkit.getScheduler().runTaskLater(coreApplication, () -> {
            if (bells < 1) {
                player.teleport(location);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6§l! §eTeleportuji... §6§l!"));
                return;
            }
            teleportFancy(player, bells - 1, location);
        }, 20L);
    }

}
