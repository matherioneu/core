package eu.matherion.core.shared.player;

import cz.maku.mommons.player.CloudPlayer;
import cz.maku.mommons.worker.WorkerReceiver;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.currency.Currency;
import eu.matherion.core.shared.currency.CurrencyService;
import eu.matherion.core.shared.permissions.luckperms.LuckPermsDependency;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class MatherionPlayer extends CloudPlayer {

    public MatherionPlayer(CloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

    public static Optional<MatherionPlayer> of(String nickname) {
        Logger logger = CoreApplication.logger();
        PlayerService playerService = WorkerReceiver.getService(CoreApplication.class, PlayerService.class);
        if (playerService == null) {
            return Optional.empty();
        }
        return playerService.getPlayer(nickname);
    }

    public static Optional<MatherionPlayer> of(Player player) {
        return of(player.getName());
    }

    public double getCachedCurrencyBalance(String currency) {
        return CurrencyService.getCachedBalance(getNickname(), currency);
    }

    public double getCurrencyBalance(String currency) {
        CurrencyService currencyService = WorkerReceiver.getService(CoreApplication.class, CurrencyService.class);
        if (currencyService == null) {
            CoreApplication.logger().severe("CurrencyService is null!");
            return 0;
        }
        return currencyService.getBalance(getNickname(), currency);
    }

    public CompletableFuture<Double> getCurrencyBalanceAsync(String currency) {
        return CompletableFuture.supplyAsync(() -> getCurrencyBalance(currency));
    }

    public String getLuckPermsRank() {
        LuckPermsDependency luckPermsDependency = CoreApplication.getDependency(LuckPermsDependency.class);
        if (luckPermsDependency == null) return null;
        LuckPerms luckPerms = luckPermsDependency.get();
        if (luckPerms == null) return null;
        User user = luckPerms.getUserManager().getUser(getNickname());
        if (user == null) return null;
        return user.getPrimaryGroup();
    }

    public boolean updateCurrencyBalance(Currency currency, double amount) {
        CurrencyService currencyService = WorkerReceiver.getService(CoreApplication.class, CurrencyService.class);
        if (currencyService == null) {
            CoreApplication.logger().severe("CurrencyService is null!");
            return false;
        }
        return currencyService.updateBalance(getNickname(), currency, amount);
    }

    public CompletableFuture<Boolean> updateCurrencyBalanceAsync(Currency currency, double amount) {
        return CompletableFuture.supplyAsync(() -> updateCurrencyBalance(currency, amount));
    }
}
