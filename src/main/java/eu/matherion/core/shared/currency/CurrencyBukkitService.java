package eu.matherion.core.shared.currency;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.event.MatherionPlayerLoadEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Optional;

import static eu.matherion.core.shared.SharedConfiguration.CURRENCY_EDIT;
import static eu.matherion.core.shared.SharedConfiguration.CURRENCY_VIEW_OTHER;


@Service(commands = true, listener = true)
public class CurrencyBukkitService {

    @Load
    private CurrencyService currencyService;

    @BukkitEvent(MatherionPlayerLoadEvent.class)
    public void onPlayerLoad(MatherionPlayerLoadEvent event) {
        MatherionPlayer matherionPlayer = event.getMatherionPlayer();
        currencyService.cachePlayer(matherionPlayer);
    }

    @BukkitCommand("currency")
    public void onCurrencyCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6§l? §ePoužití: §6/currency <měna> <hráč> [add|remove] [množství]");
            return;
        }
        String currency = args[0];
        Currency currencyInstance = currencyService.getCurrencies().get(currency);
        if (currencyInstance == null) {
            sender.sendMessage("§4§l! §cChyba, měna §4" + currency + " §cneexistuje!");
            return;
        }
        String target = sender.getName();
        if (sender instanceof ConsoleCommandSender) {
            if (args.length < 2) {
                sender.sendMessage("§4§l! §cChyba, musíš zadat hráče!");
                return;
            }
            target = args[1];
        } else {
            if (args.length > 1) {
                if (sender.hasPermission(CURRENCY_VIEW_OTHER)) {
                    target = args[1];
                } else {
                    sender.sendMessage("§4§l! §cChyba, nemáš oprávnění na zobrazení měny jiných hráčů!");
                    return;
                }
            }
        }
        Optional<MatherionPlayer> possibleMatherionPlayer = MatherionPlayer.of(target);
        if (possibleMatherionPlayer.isEmpty()) {
            sender.sendMessage("§4§l! §cChyba, tento hráč neexistuje!");
            return;
        }
        MatherionPlayer matherionPlayer = possibleMatherionPlayer.get();
        if (args.length < 3) {
            double targetCachedBalance = matherionPlayer.getCachedCurrencyBalance(currency);
            if (target.equals(sender.getName())) {
                sender.sendMessage("§8§l! §7Máš §f" + targetCachedBalance + " §7" + currencyInstance.getDisplayName() + "§7.");
            } else {
                sender.sendMessage("§8§l! §7Hráč §f" + target + " §7má §f" + targetCachedBalance + " §7" + currencyInstance.getDisplayName() + "§7.");
            }
            return;
        }
        if (!sender.hasPermission(CURRENCY_EDIT)) {
            sender.sendMessage("§4§l! §cChyba, nemáš oprávnění na úpravu měny hráčů!");
            return;
        }
        String action = args[2];
        if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            sender.sendMessage("§4§l! §cChyba, akce musí být §4add §cnebo §4remove§c!");
            return;
        }
        if (args.length < 4) {
            sender.sendMessage("§4§l! §cChyba, musíš zadat množství!");
            return;
        }
        String amountString = args[3];
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException exception) {
            sender.sendMessage("§4§l! §cChyba, množství musí být číslo!");
            return;
        }
        if (amount <= 0) {
            sender.sendMessage("§4§l! §cChyba, množství musí být větší než 0!");
            return;
        }

        String finalTarget = target;
        matherionPlayer.getCurrencyBalanceAsync(currency).thenAcceptAsync(balance -> {
            double newBalance = balance;
            if (action.equalsIgnoreCase("add")) {
                newBalance += amount;
            } else {
                newBalance -= amount;
            }
            boolean success = matherionPlayer.updateCurrencyBalance(currencyInstance, newBalance);
            if (success) {
                sender.sendMessage("§2§l! §aHráči §2" + finalTarget + " §abylo " + (action.equalsIgnoreCase("add") ? "přidáno" : "odebráno") + " §2" + amount + " §2" + currencyInstance.getDisplayName() + "§a.");
                sender.sendMessage("§2§l! §aNový stav: §2" + newBalance + " §2" + currencyInstance.getDisplayName() + "§a.");
            } else {
                sender.sendMessage("§4§l! §cChyba, nastala chyba při aktualizaci měny hráče!");
            }
        });
    }

    @BukkitCommand(value = "credits", aliases = "kredity")
    public void onCreditsCommand(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("§4§l! §cChyba, tento příkaz může použít pouze hráč!");
            sender.sendMessage("§4§l! §cPoužij §4/currency §cpro zobrazení kreditů hráčů!");
            return;
        }
        Optional<MatherionPlayer> optional = MatherionPlayer.of(sender.getName());
        if (optional.isEmpty()) {
            sender.sendMessage("§4§l! §cChyba, neexistuješ!");
            return;
        }
        MatherionPlayer matherionPlayer = optional.get();
        sender.sendMessage("§8§l! §7Máš §f" + matherionPlayer.getCachedCurrencyBalance("credits") + " §7kreditů.");
    }
}
