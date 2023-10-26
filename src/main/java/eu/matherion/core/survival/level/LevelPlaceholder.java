package eu.matherion.core.survival.level;

import eu.matherion.core.shared.placeholderapi.CorePlaceholder;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.survival.level.data.LevelData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LevelPlaceholder implements CorePlaceholder {

    @Override
    public @NotNull String identifier() {
        return "level";
    }

    @Override
    public String placeholder(MatherionPlayer matherionPlayer, List<String> parameters) {
        String type = parameters.get(0);
        LevelData levelData = LevelService.CACHE.get(matherionPlayer.getNickname());
        if (levelData == null) return null;
        if (type.equalsIgnoreCase("number")) {
            String number = String.valueOf(levelData.getLevel());
            if (parameters.size() > 1) {
                String additional = parameters.get(1);
                if (additional.equalsIgnoreCase("colored")) {
                    number = levelData.getActiveColor() + number;
                }
            }
            return number;
        }
        if (type.equalsIgnoreCase("exp")) {
            return String.valueOf(levelData.getExperience());
        }
        return null;
    }
}
