package eu.matherion.core.survival.level;

import eu.matherion.core.survival.level.data.LevelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

@AllArgsConstructor
@Getter
public class Level {

    private final int number;
    private final int requiredExperience;
    private final List<BiConsumer<Player, LevelData>> rewards;
    private final List<String> description;

}
