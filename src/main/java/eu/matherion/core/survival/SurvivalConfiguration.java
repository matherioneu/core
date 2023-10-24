package eu.matherion.core.survival;

import org.bukkit.Material;

import java.util.List;

public final class SurvivalConfiguration {

    public static final String ADMIN_VIEW_PERMISSION = "matherion.admin.view";
    public static final Material ADMIN_VIEW_MATERIAL = Material.BLAZE_POWDER;
    public static final String ADMIN_VIEW_INVENTORY_TITLE = "§4§lNáhled inventáře";
    public static final String DISABLED_PVP_WORLDS_CONFIG_PROPERTY = "disabled-pvp";
    public static final String RESIDENCE_COMMAND_BLOCKER_CATEGORY_CONFIG_PROPERTY = "residence-command-blocker";
    public static final String RESIDENCE_COMMAND_BLOCKER_EXECUTE_COMMANDS_CONFIG_PROPERTY = "execute-commands";
    public static final String RESIDENCE_COMMAND_BLOCKER_ALLOWED_COMMANDS_CONFIG_PROPERTY = "allowed-commands";
    public static final String RESIDENCE_COMMAND_BLOCKER_BLACKLIST_COMMANDS_CONFIG_PROPERTY = "blacklist-commands";
    public static final String MINEWORLD_CONFIG_PROPERTY = "mineworld";
    public static final String MINEWORLD_BLOCK_CONFIG_PROPERTY = "blocks";
    public static final String MINEWORLD_BLOCKS_MAXIMUM_CONFIG_PROPERTY = "maximum";
    public static final String MINEWORLD_WELCOME_MESSAGE_CONFIG_PROPERTY = "welcome-message";
    public static final String MINEWORLD_SPAWN_LOCATION_CONFIG_PROPERTY = "spawn-location";
    public static final String RESIDENCE_COMMAND_BLOCKER_BYPASS_PERMISSION = "matherion.residenceblock.bypass";
    public static final List<String> CHAT_TAGS = List.of("@helper", "@admin", "@owner");
    public static final List<String> BLOCKED_CHAT_MESSAGES = List.of(
            "I joined using ChatCraft from my Android device! Download it for free!",
            "I joined using ChatCraft from my iOS device! Download it for free!"
    );
    public static final int BANNED_WORDS_WARNS_KICK = 3;

}
