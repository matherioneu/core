package eu.matherion.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.maku.mommons.worker.plugin.WorkerPlugin;
import eu.matherion.core.shared.currency.CurrencyBukkitService;
import eu.matherion.core.shared.currency.CurrencyService;
import eu.matherion.core.shared.dependency.CoreDependencies;
import eu.matherion.core.shared.dependency.CoreDependency;
import eu.matherion.core.shared.dependency.CoreDependencyClassProvider;
import eu.matherion.core.shared.maintenance.MaintenanceBukkitService;
import eu.matherion.core.shared.maintenance.MaintenanceService;
import eu.matherion.core.shared.permissions.luckperms.LuckPermsDependencyProvider;
import eu.matherion.core.shared.placeholderapi.PlaceholderAPIDependencyProvider;
import eu.matherion.core.shared.player.PlayerHandlingBukkitService;
import eu.matherion.core.shared.player.PlayerService;
import eu.matherion.core.survival.CommonCommandsService;
import eu.matherion.core.survival.ProfileBukkitService;
import eu.matherion.core.survival.administrator.AdminBukkitService;
import eu.matherion.core.survival.config.ConfigBukkitService;
import eu.matherion.core.survival.glow.GlowBukkitService;
import eu.matherion.core.survival.glow.GlowService;
import eu.matherion.core.survival.level.LevelBukkitService;
import eu.matherion.core.survival.level.LevelService;
import eu.matherion.core.survival.listener.ChatListenerService;
import eu.matherion.core.survival.listener.PlayerCommonListenerService;
import eu.matherion.core.survival.listener.PvPListenerService;
import eu.matherion.core.survival.listener.SecurityListenerService;
import eu.matherion.core.survival.mineworld.MineWorldBukkitService;
import eu.matherion.core.survival.mineworld.MineWorldService;
import eu.matherion.core.survival.residence.ResidenceDependencyProvider;
import eu.matherion.core.survival.trade.TradeDependencyProvider;
import me.zort.containr.Containr;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CoreApplication extends WorkerPlugin {

    private Map<Class<? extends CoreDependencyClassProvider>, CoreDependency<?>> dependencies = Maps.newHashMap();
    private String coreType;

    public static Map<Class<? extends CoreDependencyClassProvider>, CoreDependency<?>> getDependencies() {
        return CoreApplication.getPlugin(CoreApplication.class).dependencies;
    }

    public static <D extends CoreDependency<?>> D getDependency(Class<? extends CoreDependencyClassProvider> classProviderClass) {
        return (D) CoreApplication.getDependencies().get(classProviderClass);
    }

    public static Logger logger() {
        return CoreApplication.getPlugin(CoreApplication.class).getLogger();
    }

    @Override
    public void preLoad() {
        coreType = getConfig().getString("type");
    }

    @Override
    public List<Class<?>> registerServices() {
        List<Class<?>> services = Lists.newArrayList(CurrencyService.class, CurrencyBukkitService.class, PlayerService.class, PlayerHandlingBukkitService.class, MaintenanceBukkitService.class, MaintenanceService.class);
        if (coreType.equals("survival")) {
            services.addAll(List.of(
                    AdminBukkitService.class,
                    PvPListenerService.class,
                    SecurityListenerService.class,
                    ChatListenerService.class,
                    MineWorldService.class,
                    MineWorldBukkitService.class,
                    ConfigBukkitService.class,
                    PlayerCommonListenerService.class,
                    CommonCommandsService.class,
                    LevelBukkitService.class,
                    LevelService.class,
                    ProfileBukkitService.class
            ));
            if (Bukkit.getPluginManager().getPlugin("CMI") != null) {
                services.addAll(List.of(GlowBukkitService.class, GlowService.class));
            }
        }
        return services;
    }

    @Override
    public void load() {
        Containr.init(this);

        getConfig().options().copyDefaults(true);
        saveConfig();
        Logger logger = getLogger();
        if (coreType == null) {
            logger.severe("Server type is not defined in config.yml.");
            return;
        }
        if (coreType.equals("survival")) {
            logger.info("Core plugin will be loaded for survival server.");
            dependencies = CoreDependencies.resolveDependencies(TradeDependencyProvider.class, PlaceholderAPIDependencyProvider.class, LuckPermsDependencyProvider.class, ResidenceDependencyProvider.class);
        }
    }

    @Override
    public void unload() {

    }
}
