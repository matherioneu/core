package eu.matherion.core.survival.listener;

import cz.maku.mommons.bukkit.Items;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static eu.matherion.core.survival.SurvivalConfiguration.DISABLED_PVP_WORLDS_CONFIG_PROPERTY;

@Service(listener = true)
public class PvPListenerService {

    @BukkitEvent(EntityDamageEvent.class)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        List<String> disabledPvPWorlds = coreApplication.getConfig().getStringList(DISABLED_PVP_WORLDS_CONFIG_PROPERTY);
        if (disabledPvPWorlds.contains(player.getWorld().getName())) event.setCancelled(true);
    }

    @BukkitEvent(EntityDeathEvent.class)
    public void onPlayerDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player killed)) return;
        Player killer = killed.getKiller();
        if (killer == null) return;
        killed.sendMessage("§cByl jsi zabit hráčem §c§l" + killer.getName() + "§c!");
        killer.sendMessage("§aZabil jsi hráče §a§l" + killed.getName() + "§a!");
        ItemStack head = Items.createHead(killed);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName("§7Hlava hráče §b" + killed.getName());
        head.setItemMeta(meta);
        killed.getWorld().dropItemNaturally(killed.getLocation(), head);
    }
}
