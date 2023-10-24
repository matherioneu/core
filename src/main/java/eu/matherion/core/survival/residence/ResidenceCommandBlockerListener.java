package eu.matherion.core.survival.residence;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceChangedEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import eu.matherion.core.CoreApplication;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

import static eu.matherion.core.survival.SurvivalConfiguration.*;

@AllArgsConstructor
public class ResidenceCommandBlockerListener implements Listener {

    private final CoreApplication coreApplication;
    private final Residence residence;

    @EventHandler
    public void onResidenceChange(ResidenceChangedEvent event) {
        ClaimedResidence residence = event.getTo();
        if (residence == null) return;

        ConfigurationSection configurationSection = coreApplication.getConfig().getConfigurationSection(RESIDENCE_COMMAND_BLOCKER_CATEGORY_CONFIG_PROPERTY);
        if (configurationSection == null) return;

        List<String> executeCommands = configurationSection.getStringList(String.format("%s.%s", residence.getName(), RESIDENCE_COMMAND_BLOCKER_EXECUTE_COMMANDS_CONFIG_PROPERTY));
        executeCommands.forEach(command -> Bukkit.dispatchCommand(event.getPlayer(), command));
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(RESIDENCE_COMMAND_BLOCKER_BYPASS_PERMISSION)) return;

        ClaimedResidence res = residence.getResidenceManager().getByLoc(player.getLocation());
        if (res == null) return;

        ConfigurationSection configurationSection = coreApplication.getConfig().getConfigurationSection(RESIDENCE_COMMAND_BLOCKER_CATEGORY_CONFIG_PROPERTY);
        if (configurationSection == null) return;

        String message = event.getMessage();
        List<String> blacklistCommands = configurationSection.getStringList(String.format("%s.%s", residence.getName(), RESIDENCE_COMMAND_BLOCKER_BLACKLIST_COMMANDS_CONFIG_PROPERTY));
        List<String> allowedCommands = configurationSection.getStringList(String.format("%s.%s", residence.getName(), RESIDENCE_COMMAND_BLOCKER_ALLOWED_COMMANDS_CONFIG_PROPERTY));

        for (String allowedCommand : allowedCommands) {
            if ((!allowedCommand.isEmpty()) && (message.toLowerCase().startsWith(allowedCommand.toLowerCase()))) {
                return;
            }
        }

        for (String blacklistCommand : blacklistCommands) {
            if (!blacklistCommand.isEmpty()) {
                if (blacklistCommand.equals("*")) {
                    event.setCancelled(true);
                    player.sendMessage("§4§l! §cTento příkaz je zde zakázán.");
                    break;
                }
                if (message.toLowerCase().startsWith(blacklistCommand.toLowerCase())) {
                    event.setCancelled(true);
                    player.sendMessage("§4§l! §cTento příkaz je zde zakázán.");
                }
            }
        }
    }
}
