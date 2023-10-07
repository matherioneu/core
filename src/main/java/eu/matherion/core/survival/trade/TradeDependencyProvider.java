package eu.matherion.core.survival.trade;

import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;
import eu.matherion.core.shared.dependency.CoreDependencyClassProvider;
import org.bukkit.Bukkit;

public class TradeDependencyProvider implements CoreDependencyClassProvider {

    @Override
    public Class<? extends CoreDependency<?>> getDependencyClass() {
        return TradeDependency.class;
    }

    @Override
    public boolean canLoad(CoreApplication coreApplication) {
        return Bukkit.getPluginManager().getPlugin("TradeMe") != null;
    }
}
