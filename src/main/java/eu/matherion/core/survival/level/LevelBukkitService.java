package eu.matherion.core.survival.level;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.event.MatherionPlayerLoadEvent;
import eu.matherion.core.survival.ProfileBukkitService;
import eu.matherion.core.survival.level.event.MatherionLevelUpEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

import static eu.matherion.core.survival.level.gui.LevelGUIs.buildMainLevelMenu;

@Service(listener = true, commands = true)
public class LevelBukkitService {

    @Load
    private ProfileBukkitService profileBukkitService;

    @Load
    private LevelService levelService;

    @BukkitEvent(MatherionPlayerLoadEvent.class)
    public void onPlayerLoad(MatherionPlayerLoadEvent event) {
        MatherionPlayer matherionPlayer = event.getMatherionPlayer();
        levelService.cachePlayer(matherionPlayer.getNickname());
    }

    @BukkitCommand(value = "level", aliases = {"leveling", "levels", "levely"})
    public void onLevelCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        if (args.length < 1) {
            buildMainLevelMenu(LevelService.CACHE.get(player.getName()), levelService, profileBukkitService).open(player);
            return;
        }
        if (!player.hasPermission("matherion.level.admin")) {
            player.sendMessage("§4§l! §cChyba, nemáš oprávnění na použití tohoto příkazu.");
            return;
        }
        String action = args[0];
        if (action.equalsIgnoreCase("set")) {
            if (args.length < 4) {
                player.sendMessage("§6§l! §ePoužití: §6/level [set] <hráč> <level|exp> <číslo> [expgive]");
                return;
            }
            String targetName = args[1];
            Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(targetName);
            if (optionalMatherionPlayer.isEmpty()) {
                player.sendMessage("§4§l! §cChyba, hráč §4" + targetName + " §cnení online.");
                return;
            }
            MatherionPlayer targetPlayer = optionalMatherionPlayer.get();
            String type = args[2];
            String numberRaw = args[3];
            int number;
            try {
                number = Integer.parseInt(numberRaw);
            } catch (NumberFormatException e) {
                player.sendMessage("§4§l! §cChyba, číslo §4" + numberRaw + " §cnení platné.");
                return;
            }
            if (type.equalsIgnoreCase("level")) {
                levelService.transactionData(targetName, levelData -> {
                    levelData.setLevel(number);
                    return levelData;
                }).thenAccept(success -> {
                    if (success) {
                        player.sendMessage("§2§l! §aÚspěch, nastavil jsi hráči §2" + targetName + " §alevel §2" + number + "§a.");
                        return;
                    }
                    player.sendMessage("§4§l! §cChyba, nepodařilo se nastavit level hráči §4" + targetName + "§c.");
                });
                return;
            }
            if (type.equalsIgnoreCase("exp")) {
                if (args.length == 5) {
                    if (!args[4].equalsIgnoreCase("expgive")) return;
                    levelService.giveExperience(targetName, number).thenAccept(success -> {
                        if (success) {
                            player.sendMessage("§2§l! §aÚspěch, dal jsi hráči §2" + targetName + " §azkušenosti §2" + number + "§a.");
                            return;
                        }
                        player.sendMessage("§4§l! §cChyba, nepodařilo se dát zkušenosti hráči §4" + targetName + "§c.");
                    });
                    return;
                }
                levelService.transactionData(targetName, levelData -> {
                    levelData.setExperience(number);
                    return levelData;
                }).thenAccept(success -> {
                    if (success) {
                        player.sendMessage("§2§l! §aÚspěch, nastavil jsi hráči §2" + targetName + " §azkušenosti na §2" + number + "§a.");
                        return;
                    }
                    player.sendMessage("§4§l! §cChyba, nepodařilo se nastavit zkušenosti hráči §4" + targetName + "§c.");
                });
            }
            return;
        }
        player.sendMessage("§6§l! §ePoužití: §6/level [set] <hráč> <level|exp> <číslo> [expgive]");
    }

    @BukkitEvent(MatherionLevelUpEvent.class)
    public void onLevelUp(MatherionLevelUpEvent event) {
        MatherionPlayer matherionPlayer = event.getMatherionPlayer();
        int level = event.getLevel();
        Player player = matherionPlayer.bukkit();
        player.sendMessage("⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛");
        player.sendMessage("⬛⬛⬛⬛⬛§6⬛§f⬛⬛⬛⬛⬛");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛");
        player.sendMessage("⬛⬛⬛§6⬛⬛⬛⬛⬛§f⬛⬛⬛");
        player.sendMessage("⬛⬛§6⬛⬛⬛⬛⬛⬛⬛§f⬛⬛    ");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛                  §6§lLEVEL UP");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛          §eDosáhl jsi levelu §6" + level + "§e!");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛ §eDo dalšího levelu potřebuješ §6" + levelService.getRequiredExp(level + 1) + "EXP§e!");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛");
        player.sendMessage("⬛⬛⬛⬛§6⬛⬛⬛§f⬛⬛⬛⬛");
        player.sendTitle("§6§lLevel UP", "§eDosáhl jsi levelu §6" + level + "§e!", 10, 20 * 6, 40);
    }
}
