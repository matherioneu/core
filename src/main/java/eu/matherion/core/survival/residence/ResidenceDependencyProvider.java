package eu.matherion.core.survival.residence;

import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;
import eu.matherion.core.shared.dependency.CoreDependencyClassProvider;
import org.bukkit.Bukkit;

public class ResidenceDependencyProvider implements CoreDependencyClassProvider {

    @Override
    public Class<? extends CoreDependency<?>> getDependencyClass() {
        return ResidenceDependency.class;
    }

    @Override
    public boolean canLoad(CoreApplication coreApplication) {
        return Bukkit.getPluginManager().getPlugin("Residence") != null;
    }
}
