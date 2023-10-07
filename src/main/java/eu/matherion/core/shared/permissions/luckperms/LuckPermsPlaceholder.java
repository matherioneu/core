package eu.matherion.core.shared.permissions.luckperms;

import eu.matherion.core.shared.placeholderapi.CorePlaceholder;
import eu.matherion.core.shared.player.MatherionPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuckPermsPlaceholder implements CorePlaceholder {

    @Override
    public @NotNull String identifier() {
        return "luckperms";
    }

    @Override
    public @Nullable String placeholder(MatherionPlayer matherionPlayer, List<String> parameters) {
        String type = parameters.get(0);
        if (type.equalsIgnoreCase("rank")) return matherionPlayer.getLuckPermsRank();
        return null;
    }
}
