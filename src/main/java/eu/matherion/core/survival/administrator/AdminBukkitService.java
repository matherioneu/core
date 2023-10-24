package eu.matherion.core.survival.administrator;

import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Service;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static eu.matherion.core.survival.SurvivalConfiguration.*;

@Service(listener = true)
public class AdminBukkitService {

    private Inventory createAdminViewInventory(Inventory inventory) {
        Inventory adminInventory = Bukkit.createInventory(null, inventory.getType(), ADMIN_VIEW_INVENTORY_TITLE);
        if (inventory instanceof DoubleChestInventory) {
            adminInventory = Bukkit.createInventory(null, 54, ADMIN_VIEW_INVENTORY_TITLE);
        }
        adminInventory.setContents(inventory.getContents());
        return adminInventory;
    }

    @BukkitEvent(PlayerInteractEvent.class)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Player player = event.getPlayer();
        if (!player.hasPermission(ADMIN_VIEW_PERMISSION)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().equals(ADMIN_VIEW_MATERIAL)) return;
        if (!(clickedBlock.getState() instanceof Container container)) return;
        Inventory inventory = createAdminViewInventory(container.getInventory());
        player.openInventory(inventory);
        event.setCancelled(true);
    }

    @BukkitEvent(InventoryClickEvent.class)
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.equalsIgnoreCase(ADMIN_VIEW_INVENTORY_TITLE)) return;
        event.setCancelled(true);
    }

    @BukkitEvent(PlayerInteractEntityEvent.class)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(ADMIN_VIEW_PERMISSION)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().equals(ADMIN_VIEW_MATERIAL)) return;
        event.setCancelled(true);
        Entity entity = event.getRightClicked();
        if (entity instanceof ItemFrame itemFrame) {
            ItemStack frameItem = itemFrame.getItem();
            ItemMeta meta = frameItem.getItemMeta();
            String name = "-";
            String lore = "-";
            String enchants = "-";
            String flags = "-";
            if (meta != null) {
                name = meta.getDisplayName();
                lore = String.join("\n", meta.getLore() != null ? meta.getLore() : List.of());
                enchants = String.join("\n", meta.getEnchants().keySet().stream().map(enchant -> enchant.getKey().getKey()).toList());
                flags = String.join("\n", meta.getItemFlags().stream().map(Enum::name).toList());
            }
            List.of(
                    "",
                    "§7Název: §f" + name,
                    "§7Lore: §f" + lore,
                    "§7Enchanty: §f" + enchants,
                    "§7Flags: §f" + flags,
                    "§7Material: §f" + frameItem.getType().name(),
                    "§7Poškození: §f" + frameItem.getDurability()
            ).forEach(player::sendMessage);
        }
    }
}
