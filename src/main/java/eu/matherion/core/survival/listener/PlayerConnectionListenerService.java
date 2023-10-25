package eu.matherion.core.survival.listener;

import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.event.MatherionPlayerLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Service(listener = true)
public class PlayerConnectionListenerService {

    private final String serverVersion = Bukkit.getBukkitVersion();

    @BukkitEvent(MatherionPlayerLoadEvent.class)
    public void onPlayerLoad(MatherionPlayerLoadEvent event) {
        MatherionPlayer matherionPlayer = event.getMatherionPlayer();
        Player player = matherionPlayer.bukkit();
        player.sendTitle("§9§lEconomy Survival", serverVersion, 10, 60, 10);
        matherionPlayer.executeCommand("cmi spawn");
        if (!matherionPlayer.hasPermission("cmi.command.god")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("cmi god %s false", matherionPlayer.getNickname()));
        }
        if (!matherionPlayer.hasPermission("matherion.hasglow")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("cmi glow %s false", matherionPlayer.getNickname()));
        }
    }

}
