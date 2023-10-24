package eu.matherion.core.survival.mineworld;

import com.google.common.collect.Maps;
import cz.maku.mommons.worker.annotation.Initialize;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.commons.Bukkits;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

import static eu.matherion.core.survival.SurvivalConfiguration.*;

@Service
public class MineWorldService {

    @Getter
    private final Map<Material, Double> mineableBlocks = Maps.newHashMap();
    @Getter
    private final Map<String, Integer> minedBlocks = Maps.newHashMap();
    @Getter
    private int maximumBlocks;
    @Getter
    private List<String> welcomeMessage;
    @Getter
    private Location spawnLocation;

    @Initialize
    private void registerBlocks() {
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        ConfigurationSection configurationSection = coreApplication.getConfig().getConfigurationSection(MINEWORLD_CONFIG_PROPERTY);
        if (configurationSection == null) {
            coreApplication.getLogger().warning("MineWorld config section not found!");
            return;
        }
        maximumBlocks = configurationSection.getInt(MINEWORLD_BLOCKS_MAXIMUM_CONFIG_PROPERTY);
        welcomeMessage = configurationSection.getStringList(MINEWORLD_WELCOME_MESSAGE_CONFIG_PROPERTY);
        spawnLocation = Bukkits.stringToLocation(configurationSection.getString(MINEWORLD_SPAWN_LOCATION_CONFIG_PROPERTY));
        ConfigurationSection blocksList = configurationSection.getConfigurationSection(MINEWORLD_BLOCK_CONFIG_PROPERTY);
        if (blocksList == null) {
            coreApplication.getLogger().warning("MineWorld blocks list is empty!");
            return;
        }
        int registeredBlocks = 0;
        for (String blockKey : blocksList.getKeys(false)) {
            Material material = Material.getMaterial(blockKey);
            if (material == null) {
                coreApplication.getLogger().severe("MineWorld block material '" + blockKey + "' does not exist!");
                continue;
            }
            ConfigurationSection section = blocksList.getConfigurationSection(blockKey);
            if (section == null) continue;
            double price = section.getDouble("price");
            mineableBlocks.put(material, price);
            registeredBlocks++;
        }
        coreApplication.getLogger().info("Registered " + registeredBlocks + " blocks to MineWorld.");
    }

}
