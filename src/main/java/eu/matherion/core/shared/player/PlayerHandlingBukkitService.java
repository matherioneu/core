package eu.matherion.core.shared.player;

import cz.maku.mommons.player.CloudPlayer;
import cz.maku.mommons.player.event.CloudPlayerLoadEvent;
import cz.maku.mommons.player.event.CloudPlayerPreUnloadEvent;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.event.MatherionPlayerLoadEvent;
import org.bukkit.Bukkit;

@Service(listener = true)
public class PlayerHandlingBukkitService {

    @Load
    private PlayerService playerService;

    @BukkitEvent(value = CloudPlayerLoadEvent.class)
    public void onPlayerLoad(CloudPlayerLoadEvent event) {
        CloudPlayer cloudPlayer = event.getCloudPlayer();
        MatherionPlayer matherionPlayer = new MatherionPlayer(cloudPlayer);
        playerService.addPlayer(matherionPlayer);


        MatherionPlayerLoadEvent loadEvent = new MatherionPlayerLoadEvent(matherionPlayer);
        Bukkit.getPluginManager().callEvent(loadEvent);
    }

    @BukkitEvent(value = CloudPlayerPreUnloadEvent.class)
    public void onPlayerUnload(CloudPlayerPreUnloadEvent event) {
        CloudPlayer cloudPlayer = event.getCloudPlayer();
        playerService.getPlayer(cloudPlayer.getNickname()).ifPresent(playerService::removePlayer);
    }

}
