package eu.matherion.core.shared.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CurrencyUpdateRequest {

    private final String player;
    private final Currency currency;
    private final double amount;

    public static CurrencyUpdateRequest of(String player, Currency currency, double amount) {
        return new CurrencyUpdateRequest(player, currency, amount);
    }
}
