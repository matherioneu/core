package eu.matherion.core.shared.permissions.luckperms;

import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;
import eu.matherion.core.shared.dependency.CoreDependencyClassProvider;
import org.bukkit.Bukkit;

public class LuckPermsDependencyProvider implements CoreDependencyClassProvider {

    @Override
    public Class<? extends CoreDependency<?>> getDependencyClass() {
        return LuckPermsDependency.class;
    }

    @Override
    public boolean canLoad(CoreApplication coreApplication) {
        return Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
    }
}
