package eu.matherion.core.survival;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.permissions.luckperms.LuckPermsDependency;
import eu.matherion.core.shared.permissions.luckperms.LuckPermsDependencyProvider;
import eu.matherion.core.shared.player.MatherionPlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
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

    @BukkitCommand(value = "rank", aliases = "rankexpirace")
    public void onRankCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        LuckPermsDependency luckPermsDependency = CoreApplication.getDependency(LuckPermsDependencyProvider.class);
        if (luckPermsDependency == null) {
            player.sendMessage("§4§l! §cChyba, LuckPermsDependency je null.");
            return;
        }
        LuckPerms luckPerms = luckPermsDependency.get();
        if (luckPerms == null) {
            player.sendMessage("§4§l! §cChyba, LuckPerms je null.");
            return;
        }
        User user = luckPerms.getUserManager().getUser(player.getName());
        if (user == null) {
            player.sendMessage("§4§l!Chyba, uživatel je null.");
            return;
        }
        List<Instant> expire = user.getNodes(NodeType.INHERITANCE)
                .stream()
                .filter(Node::hasExpiry)
                .map(Node::getExpiry)
                .toList();
        if (expire.isEmpty()) {
            player.sendMessage("§8§l! §7Nemáš aktivovaný žádný prémiový rank.");
            return;
        }
        expire.forEach(instant -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.GERMANY)
                    .withZone(ZoneId.systemDefault());
            String time = formatter.format(instant).replace(",", "");
            Group group = luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());
            if (group == null) return;
            String prefix = group.getCachedData().getMetaData().getPrefix();
            if (prefix == null) {
                prefix = group.getName();
            }
            player.sendMessage(String.format("§8§l! §7Tvůj rank §f%s §7vyprší §f%s§7.", ChatColor.translateAlternateColorCodes('&', prefix), time));
        });
    }
}
