package eu.matherion.core.shared.permissions.luckperms;

import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;
import eu.matherion.core.shared.placeholderapi.CorePlaceholders;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class LuckPermsDependency extends CoreDependency<LuckPerms> {

    public LuckPermsDependency(CoreApplication coreApplication) {
        super("luckperms", coreApplication);
    }

    @Override
    public @Nullable LuckPerms construct(CoreApplication coreApplication) {
        RegisteredServiceProvider<LuckPerms> provider = coreApplication.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            coreApplication.getLogger().warning("LuckPerms provider is null.");
            return null;
        }
        return provider.getProvider();
    }

    @Override
    public void init() {
        LuckPermsPlaceholder luckPermsPlaceholder = new LuckPermsPlaceholder();
        CorePlaceholders.register(luckPermsPlaceholder);
    }
}
