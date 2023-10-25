package eu.matherion.core.survival.listener;

import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.event.MatherionPlayerLoadEvent;
import eu.matherion.core.survival.residence.ResidenceDependency;
import eu.matherion.core.survival.residence.ResidenceDependencyProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;


@Service(listener = true)
public class PlayerCommonListenerService {

    private final String serverVersion = Bukkit.getBukkitVersion();

    @BukkitEvent(MatherionPlayerLoadEvent.class)
    public void onPlayerLoad(MatherionPlayerLoadEvent event) {
        MatherionPlayer matherionPlayer = event.getMatherionPlayer();
        Player player = matherionPlayer.bukkit();
        player.sendTitle("§9§lEconomy Survival", serverVersion, 10, 60, 10);
        matherionPlayer.executeCommand("cmi spawn");
        if (!matherionPlayer.hasPermission("cmi.command.god")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("cmi god %s false", matherionPlayer.getNickname()));
        }
        if (!matherionPlayer.hasPermission("matherion.hasglow")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("cmi glow %s false", matherionPlayer.getNickname()));
        }
    }

    @BukkitEvent(EntityDamageEvent.class)
    public void onDamage(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent damageByEntityEvent)) return;
        Entity damager = damageByEntityEvent.getDamager();
        if (!(damager instanceof Player player)) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame itemFrame)) return;
        ItemStack item = itemFrame.getItem();
        ResidenceDependency residenceDependency = CoreApplication.getDependency(ResidenceDependencyProvider.class);
        if (residenceDependency == null) return;
        Residence residence = residenceDependency.get();
        if (residence == null) return;
        Location frameLocation = itemFrame.getLocation();
        ClaimedResidence claimedResidence = residence.getResidenceManager().getByLoc(frameLocation);
        if (claimedResidence == null || residence.getPermsByLoc(frameLocation).playerHas(player, Flags.container, true)) {
            event.setCancelled(true);
            if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                player.sendMessage("§8§l! §7Pro vyndání itemu z rámečku musíš mít prázdnou ruku.");
                return;
            }
            CMIUser cmiUser = CMIUser.getUser(player);
            if (cmiUser.getInventory().getFreeSlots() < 1) {
                player.sendMessage("§8§l §7Pro vyndání itemu z rámečku musíš mít místo v inventáři.");
                return;
            }
            player.getInventory().setItemInMainHand(item);
            itemFrame.setItem(new ItemStack(Material.AIR, 1));
            player.sendMessage("§8§l! §7Item z rámečku ti byl dán přímo do ruky.");
        }
    }

}
