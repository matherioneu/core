package eu.matherion.core.shared.placeholderapi;

import eu.matherion.core.shared.player.MatherionPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CorePlaceholder {

    @NotNull
    String identifier();

    @Nullable
    String placeholder(MatherionPlayer matherionPlayer, List<String> parameters);

}
