package eu.matherion.core.survival.glow;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.maku.mommons.worker.annotation.Initialize;
import cz.maku.mommons.worker.annotation.Service;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;

@Service
public class GlowService {

    @Getter
    private final Map<ChatColor, Material> colors = Maps.newHashMap();
    private final Random random = new Random();

    @Initialize
    private void registerColors() {
        colors.put(ChatColor.YELLOW, Material.YELLOW_CONCRETE);
        colors.put(ChatColor.GOLD, Material.ORANGE_CONCRETE);
        colors.put(ChatColor.RED, Material.RED_CONCRETE);
        colors.put(ChatColor.GRAY, Material.LIGHT_GRAY_CONCRETE);
        colors.put(ChatColor.GREEN, Material.LIME_CONCRETE);
        colors.put(ChatColor.AQUA, Material.LIGHT_BLUE_CONCRETE);
        colors.put(ChatColor.BLACK, Material.BLACK_CONCRETE);
        colors.put(ChatColor.BLUE, Material.BLUE_CONCRETE);
        colors.put(ChatColor.DARK_AQUA, Material.CYAN_CONCRETE);
        colors.put(ChatColor.DARK_GRAY, Material.GRAY_CONCRETE);
        colors.put(ChatColor.DARK_GREEN, Material.GREEN_CONCRETE);
        colors.put(ChatColor.DARK_PURPLE, Material.PURPLE_CONCRETE);
        colors.put(ChatColor.LIGHT_PURPLE, Material.MAGENTA_CONCRETE);
        colors.put(ChatColor.WHITE, Material.WHITE_CONCRETE);
    }

    public ChatColor getRandomColor() {
        List<ChatColor> chatColors = Lists.newArrayList(colors.keySet());
        return chatColors.get(random.nextInt(chatColors.size()));
    }

    public boolean isColorValid(String color) {
        for (ChatColor value : ChatColor.values()) {
            if (value.name().equalsIgnoreCase(color)) return colors.containsKey(value);
        }
        return false;
    }

    public Material getMaterial(ChatColor color) {
        return colors.get(color);
    }

    public Optional<ChatColor> getColor(Material material) {
        return colors.keySet().stream()
                .filter(chatColor -> colors.get(chatColor).equals(material))
                .findAny();
    }
}

