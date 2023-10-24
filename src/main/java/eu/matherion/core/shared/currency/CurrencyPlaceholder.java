package eu.matherion.core.shared.currency;

import eu.matherion.core.shared.placeholderapi.CorePlaceholder;
import eu.matherion.core.shared.player.MatherionPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurrencyPlaceholder implements CorePlaceholder {

    @Override
    public @NotNull String identifier() {
        return "currency";
    }

    @Override
    public String placeholder(MatherionPlayer matherionPlayer, List<String> parameters) {
        String currency = parameters.get(0);
        String type = parameters.get(1);

        if (type.equalsIgnoreCase("balance")) {
            double balance = matherionPlayer.getCachedCurrencyBalance(currency);

            if (parameters.size() > 2) {
                String additional = parameters.get(2);
                if (additional.equalsIgnoreCase("rounded")) {
                    return String.valueOf(Math.round(balance));
                }
            }
            return String.valueOf(balance);
        }
        return null;
    }
}
