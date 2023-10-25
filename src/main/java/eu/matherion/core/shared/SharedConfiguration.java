package eu.matherion.core.shared;

import eu.matherion.core.CoreApplication;
import org.bukkit.configuration.ConfigurationSection;

public final class SharedConfiguration {

    public static final String CURRENCY_VIEW_OTHER = "matherion.currency.others";
    public static final String CURRENCY_EDIT = "matherion.currency.edit";

    public static String webhook(String identifier) {
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        ConfigurationSection section = coreApplication.getConfig().getConfigurationSection("webhooks");
        if (section == null) return null;
        return section.getString(identifier);
    }
}
