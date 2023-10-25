package eu.matherion.core.survival.glow;

import com.Zrips.CMI.Containers.CMIUser;
import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.MatherionPlayer;
import me.zort.containr.Component;
import me.zort.containr.internal.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@Service(commands = true)
public class GlowBukkitService {

    @Load
    private GlowService glowService;

    @BukkitCommand(value = "glow", aliases = "zare")
    public void onGlowCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(player);
        if (optionalMatherionPlayer.isEmpty()) return;
        MatherionPlayer matherionPlayer = optionalMatherionPlayer.get();
        Component.gui()
                .title("§lZáře")
                .rows(3)
                .prepare((g) -> {
                    for (Map.Entry<ChatColor, Material> entry : glowService.getColors().entrySet()) {
                        ChatColor chatColor = entry.getKey();
                        Material material = entry.getValue();
                        String rawName = chatColor.name().replace("_", " ").toLowerCase();
                        String name = rawName.substring(0, 1).toUpperCase() + rawName.substring(1);
                        String formattedName = String.format("%s§l%s", chatColor, name);
                        if (matherionPlayer.hasPermission("cmi.command.glow.color." + chatColor.name().toLowerCase())) {
                            g.appendElement(Component.element()
                                    .item(Items.create(material, formattedName, "§7Klikni pro vybrání záře"))
                                    .click(info -> {
                                        CMIUser cmiUser = CMIUser.getUser(player);
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("cmi glow %s true", cmiUser.getName()));
                                        if (cmiUser.getGlow() != null) {
                                            cmiUser.setGlow(cmiUser.getGlow(), false);
                                        }
                                        cmiUser.setGlow(chatColor, true);
                                        player.sendMessage("§8§l! §7Zapnul sis záři §f" + chatColor + name + "§7.");
                                    })
                                    .build()
                            );
                            continue;
                        }
                        g.appendElement(Component.element()
                                .item(Items.create(Material.BARRIER, formattedName, "§7Tuto záři nevlastníš"))
                                .build()
                        );
                    }
                    g.appendElement(22, Component.element()
                            .item(Items.create(Material.RED_DYE, "§cVypnout záři"))
                            .click(info -> {
                                CMIUser cmiUser = CMIUser.getUser(player);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("cmi glow %s false", cmiUser.getName()));
                                if (cmiUser.getGlow() != null) {
                                    cmiUser.setGlow(cmiUser.getGlow(), false);
                                }
                                player.sendMessage("§8§l! §7Vypnul sis záři.");
                            })
                            .build()
                    );
                })
                .build()
                .open(player);
    }

}
