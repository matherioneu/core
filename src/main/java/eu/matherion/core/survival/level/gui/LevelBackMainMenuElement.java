package eu.matherion.core.survival.level.gui;

import cz.maku.mommons.worker.WorkerReceiver;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.survival.ProfileBukkitService;
import me.zort.containr.ContextClickInfo;
import me.zort.containr.Element;
import me.zort.containr.internal.util.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LevelBackMainMenuElement extends Element {

    @Override
    public @Nullable ItemStack item(Player player) {
        return Items.create(Material.IRON_DOOR, "§eZpět", "§7Vrátit se do hlavního menu.");
    }

    @Override
    public void click(ContextClickInfo info) {
        ProfileBukkitService profileBukkitService = WorkerReceiver.getService(CoreApplication.class, ProfileBukkitService.class);
        if (profileBukkitService == null) return;
        profileBukkitService.openProfileMenu(info.getPlayer());
    }
}
