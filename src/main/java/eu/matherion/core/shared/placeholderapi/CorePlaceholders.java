package eu.matherion.core.shared.placeholderapi;

import com.google.common.collect.Maps;

import java.util.Map;

public final class CorePlaceholders {

    static final Map<String, CorePlaceholder> PLACEHOLDERS = Maps.newHashMap();

    public static void register(CorePlaceholder corePlaceholder) {
        PLACEHOLDERS.put(corePlaceholder.identifier(), corePlaceholder);
    }

}
