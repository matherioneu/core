package eu.matherion.core.shared.currency;

import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.event.MatherionPlayerLoadEvent;

@Service(listener = true)
public class CurrencyBukkitService {

    @Load
    private CurrencyService currencyService;

    @BukkitEvent(MatherionPlayerLoadEvent.class)
    public void onPlayerLoad(MatherionPlayerLoadEvent event) {
        MatherionPlayer matherionPlayer = event.getMatherionPlayer();
        currencyService.cachePlayer(matherionPlayer);
    }

}
