package eu.matherion.core.shared.currency;

import cz.maku.mommons.ef.converter.TypeConverter;
import cz.maku.mommons.worker.WorkerReceiver;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.currency.Currency;
import eu.matherion.core.shared.currency.CurrencyService;

public class CurrencyEntityTypeConverter implements TypeConverter<Currency, String> {

    @Override
    public String convertToColumn(Currency currency) {
        return currency.getName();
    }

    @Override
    public Currency convertToEntityField(String currencyName) {
        CurrencyService service = WorkerReceiver.getService(CoreApplication.class, CurrencyService.class);
        if (service == null) {
            CoreApplication.logger().severe("CurrencyService is null!");
            return null;
        }
        Currency currency = service.getCurrencies().get(currencyName);
        if (currency == null) {
            CoreApplication.logger().severe("Currency " + currencyName + " not found!");
            return null;
        }
        return currency;
    }
}
