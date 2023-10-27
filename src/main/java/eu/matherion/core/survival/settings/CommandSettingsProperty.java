package eu.matherion.core.survival.settings;

import eu.matherion.core.survival.settings.SettingsProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class CommandSettingsProperty extends SettingsProperty {

    public CommandSettingsProperty(String name, Material icon, String displayName, String command) {
        super(name, icon, displayName, (player, toggle) -> Bukkit.dispatchCommand(player, command.replace("{toggle}", toggle.toString())));
    }

}
