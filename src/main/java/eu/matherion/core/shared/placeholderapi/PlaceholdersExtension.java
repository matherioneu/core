package eu.matherion.core.shared.placeholderapi;

import com.google.common.collect.Lists;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.player.MatherionPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class PlaceholdersExtension extends PlaceholderExpansion {

    private final CoreApplication coreApplication;

    public PlaceholdersExtension(CoreApplication coreApplication) {
        this.coreApplication = coreApplication;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "core";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", coreApplication.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return coreApplication.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(player.getName());
        if (optionalMatherionPlayer.isEmpty()) return null;
        MatherionPlayer matherionPlayer = optionalMatherionPlayer.get();
        String[] arguments = identifier.split("_");
        for (CorePlaceholder placeholder : CorePlaceholders.PLACEHOLDERS.values()) {
            if (placeholder.identifier().equalsIgnoreCase(arguments[0])) {
                List<String> argumentsList = Lists.newArrayList(arguments);
                argumentsList.remove(0);
                return placeholder.placeholder(matherionPlayer, argumentsList);
            }
        }
        return null;
    }
}