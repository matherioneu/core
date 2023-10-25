package eu.matherion.core.survival;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.MatherionPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

@Service(commands = true)
public class CommonCommandsService {

    @BukkitCommand("checkpermission")
    public void onCheckPermCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("matherion.checkpermission")) return;
        if (args.length != 2) {
            sender.sendMessage("§6§l? §ePoužít: §6/checkpermission <hráč> <permise>");
            return;
        }
        String targetName = args[0];
        String permission = args[1];
        Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(targetName);
        if (optionalMatherionPlayer.isEmpty()) {
            sender.sendMessage("§4§l! §cChyba, hráč §4" + targetName + " §cnení online.");
            return;
        }
        MatherionPlayer matherionPlayer = optionalMatherionPlayer.get();
        sender.sendMessage("§8§l! §7Hráč §f" + matherionPlayer.getNickname() + " §7" + (matherionPlayer.hasPermission(permission) ? "§amá" : "§cnemá") + " §7oprávnění §f" + permission + "§7.");
    }

    @BukkitCommand("checkfly")
    public void onCheckFlyCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("matherion.checkfly")) return;
        if (args.length != 1) {
            sender.sendMessage("§6§l? §ePoužít: §6/checkfly <hráč>");
            return;
        }
        String targetName = args[0];
        Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(targetName);
        if (optionalMatherionPlayer.isEmpty()) {
            sender.sendMessage("§4§l! §cChyba, hráč §4" + targetName + " §cnení online.");
            return;
        }
        MatherionPlayer matherionPlayer = optionalMatherionPlayer.get();
        boolean allowedFlight = matherionPlayer.bukkit().getAllowFlight();
        boolean flying = matherionPlayer.bukkit().isFlying();
        sender.sendMessage("§8§l! §7Hráč §f" + matherionPlayer.getNickname());
        sender.sendMessage(" §8» §7Povolený let: " + (allowedFlight ? "§aano" : "§cne"));
        sender.sendMessage(" §8» §7Létá: " + (flying ? "§aano" : "§cne"));
    }

    @BukkitCommand(value = "nightvision", aliases = "nv")
    public void onNightVisionCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("matherion.nightvision")) return;
        if (!(sender instanceof Player player)) return;
        Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(player);
        if (optionalMatherionPlayer.isEmpty()) {
            sender.sendMessage("§4§l! §cChyba, neexistuješ.");
            return;
        }
        MatherionPlayer matherionPlayer = optionalMatherionPlayer.get();
        Object raw = matherionPlayer.getLocalValue("nightvision");
        if (raw == null) {
            raw = false;
        }
        boolean nightVision = (boolean) raw;
        Boolean force = null;
        if (args.length == 1) {
            String forceNightVision = args[0];
            if (forceNightVision.equalsIgnoreCase("true") || forceNightVision.equalsIgnoreCase("false")) {
                force = Boolean.parseBoolean(forceNightVision);
            } else {
                sender.sendMessage("§4§l! §cChyba, argument musí být true/false.");
                return;
            }
        }
        if (force == null) {
            force = !nightVision;
        }

        if (!force) {
            matherionPlayer.setLocalValue("nightvision", false);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage("§8§l! §cVypnul §7sis noční vidění.");
            return;
        }
        matherionPlayer.setLocalValue("nightvision", true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
        player.sendMessage("§8§l! §aZapnul §7sis noční vidění.");
    }
}
