package eu.matherion.core.shared.maintenance;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Service(listener = true, commands = true)
public class MaintenanceBukkitService {

    @Load
    private MaintenanceService maintenanceService;

    @BukkitCommand("openserver")
    public void onOpenServerCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("matherion.maintenance")) return;
        if (!maintenanceService.isServerClosed()) {
            sender.sendMessage("§4§l! §cServer je již otevřený pro veřejnost.");
            return;
        }
        String reason;
        if (args.length < 1) {
            reason = "Bez důvodu";
        } else {
            reason = String.join(" ", args);
        }
        maintenanceService.openServer(reason, sender instanceof Player ? sender.getName() : "console").thenAccept(success -> {
            if (!success) {
                sender.sendMessage("§4§l! §cNepodařilo se odeslat webhook na Discord.");
            }
        });
        sender.sendMessage("§8§l! §7Server byl otevřen pro veřejnost.");
    }

    @BukkitCommand("closeserver")
    public void onCloseServerCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("matherion.maintenance")) return;
        if (maintenanceService.isServerClosed()) {
            sender.sendMessage("§4§l! §cServer je již zavřený veřejnosti.");
            return;
        }
        String reason;
        if (args.length < 1) {
            reason = "Bez důvodu";
        } else {
            reason = String.join(" ", args);
        }
        maintenanceService.closeServer(reason, sender instanceof Player ? sender.getName() : "console").thenAccept(success -> {
            if (!success) {
                sender.sendMessage("§4§l! §cNepodařilo se odeslat webhook na Discord.");
            }
        });
        sender.sendMessage("§8§l! §7Server byl zavřen veřejnosti.");
    }
}
