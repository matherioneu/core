package eu.matherion.core.survival.listener;

import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Service;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Service(listener = true)
public class SecurityListenerService {

    @BukkitEvent(PlayerDropItemEvent.class)
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getLore() == null) return;
        if (meta.getLore().stream().anyMatch(line -> line.contains("Matherion"))) event.setCancelled(true);
    }

    @BukkitEvent(value = EntityExplodeEvent.class, priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @BukkitEvent(value = BlockPistonExtendEvent.class, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        if (type.equals(Material.SLIME_BLOCK) || type.equals(Material.HONEY_BLOCK)) event.setCancelled(true);
    }

    @BukkitEvent(value = BlockPistonRetractEvent.class, priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        if (type.equals(Material.SLIME_BLOCK) || type.equals(Material.HONEY_BLOCK)) event.setCancelled(true);
    }


}
