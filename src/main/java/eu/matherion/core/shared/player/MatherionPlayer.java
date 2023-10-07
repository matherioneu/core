package eu.matherion.core.shared.player;

import cz.maku.mommons.player.CloudPlayer;
import cz.maku.mommons.worker.WorkerReceiver;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.currency.CurrencyService;
import eu.matherion.core.shared.permissions.luckperms.LuckPermsDependency;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;

import java.util.Optional;
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

    public double getCachedBalance(String currency) {
        return CurrencyService.getCachedBalance(getNickname(), currency);
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

}
