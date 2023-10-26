package eu.matherion.core.survival.level.data;

import com.google.gson.reflect.TypeToken;
import cz.maku.mommons.Mommons;
import cz.maku.mommons.ef.converter.TypeConverter;
import org.bukkit.ChatColor;

import java.util.List;

public class LevelColorsConverter implements TypeConverter<List<ChatColor>, String> {

    @Override
    public String convertToColumn(List<ChatColor> rewards) {
        return Mommons.GSON.toJson(rewards.stream().map(ChatColor::getChar).toList());
    }

    @Override
    public List<ChatColor> convertToEntityField(String rawRewards) {
        List<Character> chars = Mommons.GSON.fromJson(rawRewards, new TypeToken<List<Character>>() {
        }.getType());
        return chars.stream().map(ChatColor::getByChar).toList();
    }
}
