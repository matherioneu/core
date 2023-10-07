package eu.matherion.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.maku.mommons.worker.plugin.WorkerPlugin;
import eu.matherion.core.shared.currency.CurrencyBukkitService;
import eu.matherion.core.shared.currency.CurrencyService;
import eu.matherion.core.shared.dependency.CoreDependencies;
import eu.matherion.core.shared.dependency.CoreDependency;
import eu.matherion.core.shared.permissions.luckperms.LuckPermsDependencyProvider;
import eu.matherion.core.shared.placeholderapi.PlaceholderAPIDependencyProvider;
import eu.matherion.core.shared.player.PlayerHandlingBukkitService;
import eu.matherion.core.shared.player.PlayerService;
import eu.matherion.core.survival.trade.TradeDependencyProvider;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CoreApplication extends WorkerPlugin {

    private Map<Class<? extends CoreDependency<?>>, CoreDependency<?>> dependencies = Maps.newHashMap();

    public static Map<Class<? extends CoreDependency<?>>, CoreDependency<?>> getDependencies() {
        return CoreApplication.getPlugin(CoreApplication.class).dependencies;
    }

    public static <D extends CoreDependency<?>> D getDependency(Class<D> dependencyClass) {
        return (D) CoreApplication.getDependencies().get(dependencyClass);
    }

    public static Logger logger() {
        return CoreApplication.getPlugin(CoreApplication.class).getLogger();
    }

    @Override
    public List<Class<?>> registerServices() {
        return Lists.newArrayList(CurrencyService.class, CurrencyBukkitService.class, PlayerService.class, PlayerHandlingBukkitService.class);
    }

    @Override
    public void load() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        Logger logger = getLogger();
        String serverType = getConfig().getString("type");
        if (serverType == null) {
            logger.severe("Server type is not defined in config.yml.");
            return;
        }
        if (serverType.equals("survival")) {
            logger.info("Core plugin will be loaded for survival server.");
            dependencies = CoreDependencies.resolveDependencies(TradeDependencyProvider.class, PlaceholderAPIDependencyProvider.class, LuckPermsDependencyProvider.class);
        }
    }

    @Override
    public void unload() {

    }
}
