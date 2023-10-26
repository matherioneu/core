package eu.matherion.core.survival.level.data;

import com.google.gson.reflect.TypeToken;
import cz.maku.mommons.Mommons;
import cz.maku.mommons.ef.converter.TypeConverter;
import cz.maku.mommons.worker.WorkerReceiver;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.currency.Currency;
import eu.matherion.core.shared.currency.CurrencyService;

import java.util.List;

public class LevelClaimedRewardsConverter implements TypeConverter<List<Integer>, String> {

    @Override
    public String convertToColumn(List<Integer> rewards) {
        return Mommons.GSON.toJson(rewards);
    }

    @Override
    public List<Integer> convertToEntityField(String rawRewards) {
        return Mommons.GSON.fromJson(rawRewards, new TypeToken<List<Integer>>(){}.getType());
    }
}
