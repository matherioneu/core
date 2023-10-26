package eu.matherion.core.survival.level.data;

import com.google.gson.reflect.TypeToken;
import cz.maku.mommons.Mommons;
import cz.maku.mommons.ef.converter.TypeConverter;
import org.bukkit.ChatColor;

import java.util.List;

public class LevelColorConverter implements TypeConverter<ChatColor, String> {

    @Override
    public String convertToColumn(ChatColor color) {
        return String.valueOf(color.getChar());
    }

    @Override
    public ChatColor convertToEntityField(String color) {
        return ChatColor.getByChar(color);
    }
}
