package eu.matherion.core.survival.settings;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.event.MatherionPlayerLoadEvent;
import me.zort.containr.Component;
import me.zort.containr.internal.util.Items;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service(listener = true)
public class SettingsBukkitService {

    @Load
    private SettingsService settingsService;

    @BukkitEvent(MatherionPlayerLoadEvent.class)
    public void onPlayerLoad(MatherionPlayerLoadEvent event) {
        CompletableFuture.runAsync(() -> {
            MatherionPlayer matherionPlayer = event.getMatherionPlayer();
            Map<String, Boolean> playerSettings = settingsService.getPlayerSettings(matherionPlayer);
            settingsService.getCache().put(matherionPlayer.getNickname(), playerSettings);
        });
    }

    @BukkitCommand(value = "settings", aliases = "nastaveni")
    public void onSettingsCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        Component.gui()
                .title("Nastavení")
                .rows(6)
                .prepare(gui -> {
                    gui.setContainer(10, Component.staticContainer()
                            .size(7, 3)
                            .init(container -> {
                                for (SettingsProperty property : settingsService.getSettingsProperties().values()) {
                                    container.appendElement(Component.element()
                                            .item(() -> {
                                                boolean toggle = settingsService.getCache().get(player.getName()).get(property.getName());
                                                return Items.create(property.getIcon(), (toggle ? "§a" : "§c") + property.getDisplayName(), "§7Stav: " + (toggle ? "§aZapnuto" : "§cVypnuto"), "", (toggle ? "§cVypnout" : "§aZapnout"));
                                            })
                                            .build()
                                    );
                                }
                            })
                            .build()
                    );
                })
                .build()
                .open(player);

    }

}
