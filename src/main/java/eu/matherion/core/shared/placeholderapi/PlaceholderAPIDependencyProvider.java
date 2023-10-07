package eu.matherion.core.shared.placeholderapi;

import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;
import eu.matherion.core.shared.dependency.CoreDependencyClassProvider;
import org.bukkit.Bukkit;

public class PlaceholderAPIDependencyProvider implements CoreDependencyClassProvider {

    @Override
    public Class<? extends CoreDependency<?>> getDependencyClass() {
        return PlaceholderAPIDependency.class;
    }

    @Override
    public boolean canLoad(CoreApplication coreApplication) {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
