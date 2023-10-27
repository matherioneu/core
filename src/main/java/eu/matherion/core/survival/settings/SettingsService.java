package eu.matherion.core.survival.settings;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import cz.maku.mommons.ExceptionResponse;
import cz.maku.mommons.Mommons;
import cz.maku.mommons.Response;
import cz.maku.mommons.worker.annotation.Initialize;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.player.MatherionPlayer;
import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static eu.matherion.core.survival.SurvivalConfiguration.PLAYER_SETTINGS_STORAGE_KEY;

@Service
public class SettingsService {

    private final CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
    @Getter
    private final Map<String, SettingsProperty> settingsProperties = Maps.newHashMap();
    @Getter
    private final Map<String, Map<String, Boolean>> cache = Maps.newHashMap();

    @Initialize
    private void registerSettings() {
        settingsProperties.put("flyonjoin", new CommandSettingsProperty("flyonjoin", Material.FEATHER, "Letání při připojení", "cmi fly {toggle}"));
        settingsProperties.put("godonjoin", new CommandSettingsProperty("godonjoin", Material.ENCHANTED_GOLDEN_APPLE, "Nesmrtelnost při připojení", "cmi god {toggle}"));
        settingsProperties.put("nightvisiononjoin", new CommandSettingsProperty("nightvisiononjoin", Material.ENDER_EYE, "Noční vidění při připojení", "nv true"));
        settingsProperties.put("scoreboard", new CommandSettingsProperty("scoreboard", Material.PAPER, "Tabulka", "tab scoreboard"));
        settingsProperties.put("trade", new CommandSettingsProperty("trade", Material.EMERALD, "Trade", "trade toggle"));
    }

    @NotNull
    public Map<String, Boolean> getPlayerSettings(MatherionPlayer matherionPlayer) {
        Object raw = matherionPlayer.getValue(PLAYER_SETTINGS_STORAGE_KEY);
        if (raw == null) raw = Mommons.GSON.toJson(Maps.newHashMap());
        String json = (String) raw;
        return Mommons.GSON.fromJson(json, new TypeToken<Map<String, Boolean>>() {
        }.getType());
    }

    @SuppressWarnings("ConstantConditions")
    public boolean toggleSettingsProperty(MatherionPlayer matherionPlayer, SettingsProperty settingsProperty) {
        Map<String, Boolean> playerSettings = getPlayerSettings(matherionPlayer);
        boolean value = playerSettings.getOrDefault(settingsProperty.getName(), false);
        playerSettings.put(settingsProperty.getName(), !value);
        Response response = matherionPlayer.setValue(PLAYER_SETTINGS_STORAGE_KEY, Mommons.GSON.toJson(playerSettings));
        if (Response.isValid(response) && !Response.isException(response)) {
            cache.put(matherionPlayer.getNickname(), playerSettings);
            return true;
        }
        coreApplication.getLogger().severe("Failed to save player settings for " + matherionPlayer.getNickname() + "!");
        ExceptionResponse exceptionResponse = Response.getExceptionResponse(response);
        exceptionResponse.getException().printStackTrace();
        return false;
    }

    public CompletableFuture<Boolean> toggleSettingsPropertyAsync(MatherionPlayer matherionPlayer, SettingsProperty settingsProperty) {
        return CompletableFuture.supplyAsync(() -> toggleSettingsProperty(matherionPlayer, settingsProperty));
    }

}
