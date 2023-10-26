package eu.matherion.core.survival.level.gui;

import com.google.common.collect.Lists;
import cz.maku.mommons.utils.Pair;
import cz.maku.mommons.utils.Texts;
import eu.matherion.core.survival.ProfileBukkitService;
import eu.matherion.core.survival.level.Level;
import eu.matherion.core.survival.level.LevelService;
import eu.matherion.core.survival.level.data.LevelData;
import me.zort.containr.Component;
import me.zort.containr.GUI;
import me.zort.containr.PagedContainer;
import me.zort.containr.internal.util.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public final class LevelGUIs {

    public static List<Pair<ChatColor, Material>> COLOR_ICONS = Lists.newArrayList(
            new Pair<>(ChatColor.GRAY, Material.LIGHT_GRAY_DYE),
            new Pair<>(ChatColor.DARK_GRAY, Material.GRAY_DYE),
            new Pair<>(ChatColor.WHITE, Material.WHITE_DYE),
            new Pair<>(ChatColor.RED, Material.RED_DYE),
            new Pair<>(ChatColor.DARK_RED, Material.REDSTONE),
            new Pair<>(ChatColor.GOLD, Material.ORANGE_DYE),
            new Pair<>(ChatColor.YELLOW, Material.YELLOW_DYE),
            new Pair<>(ChatColor.GREEN, Material.LIME_DYE),
            new Pair<>(ChatColor.DARK_GREEN, Material.GREEN_DYE),
            new Pair<>(ChatColor.AQUA, Material.LIGHT_BLUE_DYE),
            new Pair<>(ChatColor.DARK_AQUA, Material.CYAN_DYE),
            new Pair<>(ChatColor.BLUE, Material.BLUE_DYE),
            new Pair<>(ChatColor.LIGHT_PURPLE, Material.PINK_DYE),
            new Pair<>(ChatColor.DARK_PURPLE, Material.PURPLE_DYE)
    );

    public static GUI buildLevelColorsMenu(LevelData levelData, LevelService levelService) {
        return Component.gui()
                .title("Survival Leveling")
                .rows(6)
                .prepare(gui -> {
                    PagedContainer colorsContainer = Component.pagedContainer()
                            .size(3, 3)
                            .init(container -> {
                                for (Pair<ChatColor, Material> colorIcon : COLOR_ICONS) {
                                    ChatColor color = colorIcon.getFirst();
                                    Material material = colorIcon.getSecond();
                                    boolean unlocked = levelData.getColors().contains(color);
                                    boolean active = levelData.getActiveColor().equals(color);
                                    String rawName = color.name().replace("_", " ").toLowerCase();
                                    String name = color + rawName.substring(0, 1).toUpperCase() + rawName.substring(1);
                                    String clickable = unlocked ? "§eVybrat tuto barvu!" : "§cTuto barvu zatím nevlastníš!";
                                    if (active) {
                                        clickable = "§aTato barva je aktivní!";
                                    }
                                    container.appendElement(Component.element()
                                            .item(Items.create(material, name, "§7Visualizace:", " §8• §7Level " + color + levelData.getLevel(), " §8• §7Zkušenosti: " + color + levelData.getExperience(), "", clickable))
                                            .click(info -> {
                                                Player player = info.getPlayer();
                                                if (!unlocked) {
                                                    player.sendMessage("§4§l! §cTuto barvu zatím nevlastníš!");
                                                    return;
                                                }
                                                if (active) {
                                                    player.sendMessage("§4§l! §cTuto barvu máš již aktivní!");
                                                    return;
                                                }
                                                levelService.transactionData(player.getName(), data -> {
                                                    data.setActiveColor(color);
                                                    return data;
                                                }).thenAccept(success -> {
                                                    if (success) {
                                                        player.sendMessage("§8§l! §7Barva levelu byla změněna na §f" + color + color.name().replace("_", "").toLowerCase() + "§7.");
                                                        return;
                                                    }
                                                    player.sendMessage("§4§l! §cChyba, nepodařilo se změnit barvu levelu!");
                                                });
                                                info.close();
                                            })
                                            .build()
                                    );
                                }
                            })
                            .build();
                    gui.setContainer(12, colorsContainer);
                    gui.setElement(20, Component.element()
                            .item(() -> !colorsContainer.isFirstPage() ? (
                                    Items.createSkull(
                                            "§eStrana " + (colorsContainer.getCurrentPage() - 1),
                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVkNzg4MjI1NzYzMTdiMDQ4ZWVhOTIyMjdjZDg1ZjdhZmNjNDQxNDhkY2I4MzI3MzNiYWNjYjhlYjU2ZmExIn19fQ=="
                                    )
                            ) : null)
                            .click(info -> {
                                colorsContainer.previousPage();
                                info.getGui().update(info.getPlayer());
                            })
                            .build());
                    gui.setElement(24, Component.element()
                            .item(() -> !colorsContainer.isLastPage() ? (
                                    Items.createSkull(
                                            "§eStrana " + (colorsContainer.getCurrentPage() + 1),
                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE1NDQ1ZGExNmZhYjY3ZmNkODI3ZjcxYmFlOWMxZDJmOTBjNzNlYjJjMWJkMWVmOGQ4Mzk2Y2Q4ZTgifX19"
                                    )
                            ) : null)
                            .click(info -> {
                                colorsContainer.nextPage();
                                info.getGui().update(info.getPlayer());
                            })
                            .build());
                    gui.setContainer(48, Component.staticContainer()
                            .size(3, 1)
                            .init(container -> {
                                int nextLevel = levelData.getLevel() + 1;
                                container.setElement(1, new LevelInformationsGUIElement(levelData, levelService.createProgressBar(nextLevel, levelData.getExperience(), 50, "§6|", "§8|"), levelService.getPercentage(nextLevel, levelData.getExperience()), levelService.getRequiredExp(nextLevel), true));
                                container.setElement(0, new LevelBackMainMenuElement());
                            })
                            .build()
                    );
                })
                .build();
    }

    public static GUI buildMainLevelMenu(LevelData levelData, LevelService levelService, ProfileBukkitService profileBukkitService) {
        return Component.gui()
                .title("Survival Leveling")
                .rows(6)
                .prepare(gui -> {
                    PagedContainer rewardsContainer = Component.pagedContainer()
                            .size(9, 2)
                            .init(container -> {
                                for (Level level : levelService.getLevels()) {
                                    int number = level.getNumber();
                                    boolean claimed = levelData.getClaimedRewards().contains(number);
                                    boolean unlocked = levelData.getLevel() >= number;
                                    String name = (claimed || !unlocked ? "§c" : "§a") + "Level Odměna " + number;
                                    List<String> lore = Lists.newArrayList("");
                                    lore.addAll(level.getDescription());
                                    lore.add("");
                                    lore.add(claimed ? "§aTuto odměnu sis již vyzvedl!" : unlocked ? "§eTuto odměnu si lze vyzvednout!" : "§cTuto odměnu si ještě nelze vyzvednout!");
                                    Material material = Material.FURNACE_MINECART;
                                    if (unlocked) {
                                        material = Material.CHEST_MINECART;
                                    }
                                    if (claimed) {
                                        material = Material.MINECART;
                                    }
                                    container.appendElement(Component.element()
                                            .item(Items.create(material, name, lore.toArray(new String[0])))
                                            .click(info -> {
                                                Player clicker = info.getPlayer();
                                                if (!unlocked) {
                                                    clicker.sendMessage("§4§l! §cTuhle odměnu si ještě nelze vyzvednout!");
                                                    return;
                                                }
                                                if (claimed) {
                                                    clicker.sendMessage("§4§l! §cTuto odměnu jsi již vyzvedl!");
                                                    return;
                                                }
                                                info.close();
                                                levelService.claimReward(clicker.getName(), level).thenAccept(success -> {
                                                    if (success) {
                                                        clicker.sendMessage("§8§l! §7Vyzvedl sis odměnu za level §f" + number + "§7.");
                                                        return;
                                                    }
                                                    clicker.sendMessage("§4§l! §cChyba, nepodařilo se vyzvednout odměnu!");
                                                });
                                            })
                                            .build()
                                    );
                                }
                            })
                            .build();
                    gui.setContainer(0, rewardsContainer);
                    gui.setElement(18, Component.element()
                            .item(() -> !rewardsContainer.isFirstPage() ? (
                                    Items.createSkull(
                                            "§eStrana " + (rewardsContainer.getCurrentPage() - 1),
                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVkNzg4MjI1NzYzMTdiMDQ4ZWVhOTIyMjdjZDg1ZjdhZmNjNDQxNDhkY2I4MzI3MzNiYWNjYjhlYjU2ZmExIn19fQ=="
                                    )
                            ) : null)
                            .click(info -> {
                                rewardsContainer.previousPage();
                                info.getGui().update(info.getPlayer());
                            })
                            .build());
                    gui.setElement(26, Component.element()
                            .item(() -> !rewardsContainer.isLastPage() ? (
                                    Items.createSkull(
                                            "§eStrana " + (rewardsContainer.getCurrentPage() + 1),
                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE1NDQ1ZGExNmZhYjY3ZmNkODI3ZjcxYmFlOWMxZDJmOTBjNzNlYjJjMWJkMWVmOGQ4Mzk2Y2Q4ZTgifX19"
                                    )
                            ) : null)
                            .click(info -> {
                                rewardsContainer.nextPage();
                                info.getGui().update(info.getPlayer());
                            })
                            .build());
                    gui.setContainer(48, Component.staticContainer()
                            .size(3, 1)
                            .init(container -> {
                                int nextLevel = levelData.getLevel() + 1;
                                container.setElement(1, new LevelInformationsGUIElement(levelData, levelService.createProgressBar(nextLevel, levelData.getExperience(), 50, "§6|", "§8|"), levelService.getPercentage(nextLevel, levelData.getExperience()), levelService.getRequiredExp(nextLevel), false));
                                container.setElement(0, new LevelBackMainMenuElement());
                                List<String> colorsLore = Lists.newArrayList(Texts.createTextBlock(35, "§7Za jednotlivé milníky levelů získáváš také barvy, které pak můžeš uplatnit tam, kde se tvůj level zobrazuje §8(chat, tab, profil, ...)§7! Díky tomu dáš ještě víc najevo, že jsi zkušený hráč."));
                                colorsLore.add("");
                                colorsLore.add("§eZměnit barvu");
                                container.setElement(2, Component.element()
                                        .item(Items.create(Material.YELLOW_DYE, "§eBarva levelu", colorsLore.toArray(new String[0])))
                                        .click(info -> buildLevelColorsMenu(levelData, levelService).open(info.getPlayer()))
                                        .build()
                                );
                            })
                            .build()
                    );
                })
                .build();
    }

}
