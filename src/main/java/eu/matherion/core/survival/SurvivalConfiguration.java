package eu.matherion.core.survival;

import org.bukkit.Material;

import java.util.List;

public final class SurvivalConfiguration {

    public static final String ADMIN_VIEW_PERMISSION = "matherion.admin.view";
    public static final Material ADMIN_VIEW_MATERIAL = Material.BLAZE_POWDER;
    public static final String ADMIN_VIEW_INVENTORY_TITLE = "§4§lNáhled inventáře";
    public static final String DISABLED_PVP_WORLDS_CONFIG_PROPERTY = "disabled-pvp";
    public static final List<String> CHAT_TAGS = List.of("@helper", "@admin", "@owner");
    public static final List<String> BLOCKED_CHAT_MESSAGES = List.of(
            "I joined using ChatCraft from my Android device! Download it for free!",
            "I joined using ChatCraft from my iOS device! Download it for free!"
    );
    public static final int BANNED_WORDS_WARNS_KICK = 3;

}
