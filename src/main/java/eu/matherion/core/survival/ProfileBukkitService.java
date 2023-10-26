package eu.matherion.core.survival;

import com.google.common.collect.Lists;
import cz.maku.mommons.utils.Texts;
import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.currency.CurrencyData;
import eu.matherion.core.shared.currency.CurrencyService;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.survival.level.data.LevelData;
import eu.matherion.core.survival.level.gui.LevelInformationsGUIElement;
import eu.matherion.core.survival.level.LevelService;
import me.zort.containr.Component;
import me.zort.containr.internal.util.Items;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@Service(commands = true)
public class ProfileBukkitService {

    public static final String INSTAGRAM_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjViM2YyY2ZhMDczOWM0ZTgyODMxNmYzOWY5MGIwNWJjMWY0ZWQyN2IxZTM1ODg4NTExZjU1OGQ0Njc1In19fQ==";
    @Load
    private LevelService levelService;

    @BukkitCommand("profil")
    public void onProfileCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        openProfileMenu(player);
    }

    public void openProfileMenu(Player player) {
        Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(player);
        if (optionalMatherionPlayer.isEmpty()) {
            player.sendMessage("§4§l! §cNepodařilo se načíst tvůj profil!");
            return;
        }
        MatherionPlayer matherionPlayer = optionalMatherionPlayer.get();
        Component.gui()
                .title("Můj profil")
                .rows(6)
                .prepare(gui -> {
                    LevelData levelData = LevelService.CACHE.get(player.getName());
                    if (levelData == null) return;
                    int nextLevel = levelData.getLevel() + 1;
                    int requiredExp = levelService.getRequiredExp(nextLevel);
                    List<String> info = Lists.newArrayList("§7Rank: §f" + matherionPlayer.getLuckPermsRank(), "§7Survival Level: §6" + levelData.getLevel(), "§7Zkušenosti do dalšího levelu: §6" + (requiredExp - levelData.getExperience()), "§7Měny:", " §8• §7Peníze: §f0$");
                    List<CurrencyData> currencies = CurrencyService.CACHE.get(player.getName());
                    if (currencies == null) return;
                    for (CurrencyData currencyData : currencies) {
                        info.add(" §8• §7" + currencyData.getCurrency().getDisplayName() + ": §b" + currencyData.getAmount());
                    }
                    info.addAll(List.of("§7Mise: §a0", "§7Hlasy: §d0"));
                    gui.setElement(13, Component.element()
                            .item(Items.create(Material.PAPER, "§a" + player.getName(), info.toArray(new String[0])))
                            .build()
                    );
                    gui.setElement(12, Component.element()
                            .item(Items.createSkull("§6Sociální sítě", Lists.newArrayList("§7Spravuj své veřejné sociální sítě, které vidí ostatní hráči.", "", "§eSpravovat sítě"), INSTAGRAM_HEAD))
                            .build()
                    );
                    List<String> missionsLore = Lists.newArrayList(Texts.createTextBlock(35, "§7Dokončením misí získáš odměny, kde nalezneš spoustu §bKreditů, §6Level Zkušeností, §aPeněz, §7itemů a více! Tak neváhej se ponořit do plnění a nech se obklopit světem odměn."));
                    missionsLore.add("");
                    missionsLore.add("§eZobrazit dostupné mise");
                    gui.setElement(14, Component.element()
                            .item(Items.create(Material.WRITABLE_BOOK, "§eMise", missionsLore.toArray(new String[0])))
                            .build()
                    );
                    gui.setElement(22, new LevelInformationsGUIElement(levelData, levelService.createProgressBar(nextLevel, levelData.getExperience(), 50, "§6|", "§8|"), levelService.getPercentage(nextLevel, levelData.getExperience()), requiredExp, true));
                    List<String> shopLore = Lists.newArrayList(Texts.createTextBlock(35, "§7Obchod s itemy nabízí nekonečný sortiment vanilla itemů a bloků, které si můžeš zakoupit za těžce vydělané a získané peníze. Nechce se ti v těžebním světě či přírodě hledat potřebné suroviny? Tak neváhej a nakupuj!"));
                    shopLore.add("");
                    shopLore.add("§eNakupovat");
                    gui.setElement(23, Component.element()
                            .item(Items.create(Material.EMERALD, "§aObchod s itemy", shopLore.toArray(new String[0])))
                            .build()
                    );
                    gui.setContainer(36, Component.staticContainer()
                            .size(9, 1)
                            .init(container -> {
                                for (int i = 0; i < 9; i++) {
                                    container.appendElement(Component.element(Items.create(Material.BLACK_STAINED_GLASS_PANE, "", "")).build());
                                }
                            })
                            .build()
                    );

                    List<String> shopkeeperLore = Lists.newArrayList(Texts.createTextBlock(35, "§7U obchodníka si můžeš zakoupit nejen klíče k boxům, ale také prémiové VIP balíčky, díky kterým získáš spousta výhod napříč celým serverem a tvé jméno bude zářit všude, kde se jen objevíš! Balíčky jsou k dispozici v různých variantách na různé časové období. Tak to běž omrknout!"));
                    shopkeeperLore.add("");
                    shopkeeperLore.add("§eNavštívit obchodníka");
                    gui.setElement(49, Component.element()
                            .item(Items.create(Material.GOLD_INGOT, "§6Obchodník", shopkeeperLore.toArray(new String[0])))
                            .build()
                    );
                })
                .build()
                .open(player);
    }

}