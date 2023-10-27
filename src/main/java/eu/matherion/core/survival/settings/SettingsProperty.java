package eu.matherion.core.survival.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@AllArgsConstructor
@Getter
public class SettingsProperty {

    private final String name;
    private final Material icon;
    private final String displayName;
    private final BiConsumer<Player, Boolean> toggleAction;

}
