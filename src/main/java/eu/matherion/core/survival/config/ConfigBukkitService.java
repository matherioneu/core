package eu.matherion.core.survival.config;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.commons.Bukkits;
import eu.matherion.core.survival.mineworld.MineWorldService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Service(commands = true)
public class ConfigBukkitService {

    private final CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
    @Load
    private MineWorldService mineWorldService;

    private void reload() {
        mineWorldService.registerBlocks();
    }

    @BukkitCommand("config")
    public void onConfigCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("matherion.config")) return;
        if (args.length == 0) {
            sender.sendMessage("§6§l? §ePoužití: §6/config <reload|set|get> <path> <value>");
            return;
        }
        String action = args[0];
        if (action.equalsIgnoreCase("reload")) {
            coreApplication.reloadConfig();
            reload();
            sender.sendMessage("§2§l! §aConfig byl úspěšně reloadnut!");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage("§6§l? §ePoužití: §6/config <reload|set|get> <path> <value>");
            return;
        }
        String path = args[1];
        if (action.equalsIgnoreCase("get")) {
            sender.sendMessage("§8§l! §7Hodnota: §f" + coreApplication.getConfig().get(path));
            return;
        }
        String value = args[2];
        if (value.contains("{location}")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§4§l! §cJako konzole nemůžeš použít placeholder.");
                return;
            }
            value = value.replace("{location}", Bukkits.locationToString(player.getLocation()));
        }
        if (action.equalsIgnoreCase("set")) {
            coreApplication.getConfig().set(path, value);
            coreApplication.saveConfig();
            reload();
            sender.sendMessage("§2§l! §aHodnota byla úspěšně nastavena!");
            return;
        }
        sender.sendMessage("§6§l? §ePoužití: §6/config <reload|set|get> <path> <value>");
    }

}
