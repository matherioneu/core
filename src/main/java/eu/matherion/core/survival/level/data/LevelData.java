package eu.matherion.core.survival.level.data;

import cz.maku.mommons.ef.annotation.AttributeConvert;
import cz.maku.mommons.ef.annotation.Entity;
import cz.maku.mommons.ef.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.List;

@Entity(name = "core_levels")
@Getter
@Setter
public class LevelData {

    @Id
    private String player;
    private int level;
    private int experience;
    @AttributeConvert(converter = LevelClaimedRewardsConverter.class)
    private List<Integer> claimedRewards;
    @AttributeConvert(converter = LevelColorsConverter.class)
    private List<ChatColor> colors;
    @AttributeConvert(converter = LevelColorConverter.class)
    private ChatColor activeColor;

}
