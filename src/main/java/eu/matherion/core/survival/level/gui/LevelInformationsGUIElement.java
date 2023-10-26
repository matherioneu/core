package eu.matherion.core.survival.level.gui;

import com.google.common.collect.Lists;
import cz.maku.mommons.utils.Texts;
import eu.matherion.core.survival.level.data.LevelData;
import me.zort.containr.ContextClickInfo;
import me.zort.containr.Element;
import me.zort.containr.internal.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LevelInformationsGUIElement extends Element {

    private final LevelData levelData;
    private final String progressBar;
    private final int percentage;
    private final int requiredExp;
    private final boolean clickable;

    public LevelInformationsGUIElement(LevelData levelData, String progressBar, int percentage, int requiredExp, boolean clickable) {
        this.levelData = levelData;
        this.progressBar = progressBar;
        this.percentage = percentage;
        this.requiredExp = requiredExp;
        this.clickable = clickable;
    }

    @Override
    public @Nullable ItemStack item(Player player) {
        List<String> info = Lists.newArrayList(Texts.createTextBlock(35, "§7Svou aktivitu na serveru můžeš prezentovat díky level systému. Pro dosažení jednotlivých levelů potřebuješ zkušenosti §8(EXP)§7, které získáš hraním na serveru, plněním Úkolů a Misí, točením boxů, těžením v MineWorldu a dalšími aktivitami, které Survival nabízí. Za dosažení levelů si můžeš vyzvednout odměny, v kterých nalezneš plno zajímavých věcí!"));
        info.add("");
        info.add(
                String.format(
                        "§6Level §e%s §6%s §6%s%s",
                        levelData.getLevel(),
                        progressBar,
                        percentage,
                        "%"
                )
        );
        info.add("");
        info.add("§7Zkušenosti do dalšího levelu: §e" + (requiredExp - levelData.getExperience()));
        if (clickable) {
            info.add("");
            info.add("§eZobrazit tvé odměny");
        }
        return Items.create(Material.BREWING_STAND, "§aSurvival Leveling", info.toArray(new String[0]));
    }

    @Override
    public void click(ContextClickInfo info) {
        Bukkit.dispatchCommand(info.getPlayer(), "level");
    }
}
