package eu.matherion.core.survival.mineworld;

import cz.maku.mommons.worker.annotation.BukkitCommand;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.commons.Bukkits;
import eu.matherion.core.survival.residence.ResidenceDependency;
import eu.matherion.core.survival.residence.ResidenceDependencyProvider;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Map;

@Service(listener = true, commands = true)
public class MineWorldBukkitService {

    @Load
    private MineWorldService mineWorldService;

    @BukkitEvent(BlockBreakEvent.class)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase("MineWorld")) return;
        Block block = event.getBlock();
        ResidenceDependency residenceDependency = CoreApplication.getDependency(ResidenceDependencyProvider.class);
        if (residenceDependency.get().getResidenceManager().getByLoc(block.getLocation()) != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l! §cNičením na spawnu nedostáváš peníze §4§l!"));
            return;
        }
        Material type = block.getType();
        Double price = mineWorldService.getMineableBlocks().get(type);
        if (price == null) return;
        Integer minedBlocks = mineWorldService.getMinedBlocks().get(player.getName());
        if (minedBlocks == null) {
            minedBlocks = 0;
        }
        if (minedBlocks >= mineWorldService.getMaximumBlocks()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l! §cDosáhl jsi maximálního počtu těžených bloků za den §4§l!"));
            return;
        }
        if (minedBlocks % 500 == 0) {
            // TODO: 25.10.2023 give level exp
        }
        mineWorldService.getMinedBlocks().put(player.getName(), minedBlocks + 1);
        // TODO: 25.10.2023 give money
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("§6+§e%s$ §8(§7%s§8/§7%s§8)", price, minedBlocks + 1, mineWorldService.getMaximumBlocks())));

    }

    @BukkitEvent(EntityDamageEvent.class)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player) && !entity.getWorld().getName().equalsIgnoreCase("MineWorld")) return;
        event.setCancelled(true);
    }

    @BukkitEvent(PlayerChangedWorldEvent.class)
    public void onMineWorldEntrance(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase("MineWorld")) return;
        mineWorldService.getWelcomeMessage().forEach(line -> {
            for (Map.Entry<Material, Double> entry : mineWorldService.getMineableBlocks().entrySet()) {
                line = line.replace(String.format("{%s}", entry.getKey().name()), String.valueOf(entry.getValue()));
            }
            line = line.replace("{maximum}", String.valueOf(mineWorldService.getMaximumBlocks()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        });
    }

    @BukkitCommand(value = "mineworld", aliases = "mw")
    public void onMineWorldCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        Bukkits.teleportFancy(player, 3, mineWorldService.getSpawnLocation());
    }

    @BukkitCommand(value = "mineworldrtp", aliases = "mwrtp")
    public void onMineWorldRtp(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        Bukkits.teleportFancy(player, 3, new Location(Bukkit.getWorld("MineWorld"), Math.random() * 1000, 255, Math.random() * 1000));
    }
}
