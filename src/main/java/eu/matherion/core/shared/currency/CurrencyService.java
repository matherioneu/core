package eu.matherion.core.shared.currency;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.maku.mommons.cache.ExpiringMap;
import cz.maku.mommons.worker.annotation.*;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.placeholderapi.CorePlaceholders;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.PlayerService;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service(scheduled = true)
public class CurrencyService {

    @Getter
    private final Map<String, Currency> currencies = Maps.newHashMap();
    public static final ExpiringMap<String, List<CurrencyData>> CACHE = ExpiringMap.from(2, ChronoUnit.MINUTES);
    private CurrencyRepository repository;
    @Load
    private PlayerService playerService;


    @Initialize
    private void initialization() {
        repository = new CurrencyRepository();

        CurrencyPlaceholder currencyPlaceholder = new CurrencyPlaceholder();
        CorePlaceholders.register(currencyPlaceholder);

    }

    @Initialize
    private void registerCurrencies() {
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        ConfigurationSection currenciesSection = coreApplication.getConfig().getConfigurationSection("currencies");
        if (currenciesSection == null) {
            coreApplication.getLogger().warning("Currencies config section doesn't exist.");
            return;
        }
        for (String currencyId : currenciesSection.getKeys(false)) {
            ConfigurationSection currencySection = currenciesSection.getConfigurationSection(currencyId);
            if (currencySection == null) {
                coreApplication.getLogger().warning("Currency " + currencyId + " doesn't exist.");
                continue;
            }
            String displayName = currencySection.getString("display-name");
            if (displayName == null) {
                coreApplication.getLogger().warning("Currency " + currencyId + " doesn't have display name.");
                continue;
            }
            Currency currency = new Currency(currencyId, displayName);
            registerCurrency(currency);
            coreApplication.getLogger().info("Currency " + currencyId + " has been registered.");
        }
    }

    @Repeat(delay = 20, period = 20 * 10)
    @Async
    private void synchronize() {
        List<String> players = playerService.getPlayers().values().stream().map(MatherionPlayer::getNickname).toList();
        if (players.isEmpty()) return;
        List<CurrencyData> currencies = repository.selectByPlayers(players);
        Map<String, List<CurrencyData>> data = Maps.newHashMap();
        for (CurrencyData currency : currencies) {
            MatherionPlayer player = currency.getPlayer();
            if (player == null) {
                continue;
            }
            String nickname = player.getNickname();
            if (data.containsKey(nickname)) {
                data.get(nickname).add(currency);
            } else {
                data.put(nickname, Lists.newArrayList(currency));
            }
        }

        for (String player : players) {
            if (!data.containsKey(player)) {
                data.put(player, Lists.newArrayList());
            }
        }

        for (Map.Entry<String, List<CurrencyData>> entry : data.entrySet()) {
            String nickname = entry.getKey();
            List<CurrencyData> value = entry.getValue();
            CACHE.renew(nickname, value);
        }
    }

    public void registerCurrency(Currency currency) {
        currencies.put(currency.getName(), currency);
    }

    public void unregisterCurrency(Currency currency) {
        currencies.remove(currency.getName());
    }

    protected void cachePlayer(MatherionPlayer player) {
        CompletableFuture.supplyAsync(() -> repository.select("player", player.getNickname())).thenAcceptAsync(currencies -> {
            List<String> currencyKeys = currencies.stream().map(currency -> currency.getCurrency().getName()).toList();
            for (Currency currency : this.currencies.values()) {
                if (!currencyKeys.contains(currency.getName())) {
                    CurrencyData currencyData = new CurrencyData();
                    currencyData.setCurrency(currency);
                    currencyData.setPlayer(player);
                    currencyData.setAmount(0);
                    try {
                        repository.create(currencyData);
                    } catch (IllegalAccessException e) {
                        CoreApplication.logger().severe("Cannot create currency data for player " + player.getNickname() + " and currency " + currency.getName());
                        e.printStackTrace();
                        continue;
                    }
                    currencies.add(currencyData);
                }
            }
            CACHE.renew(player.getNickname(), currencies);
        });
    }

    public static double getCachedBalance(String nickname, String currency) {
        Logger logger = CoreApplication.logger();
        List<CurrencyData> balances = CACHE.get(nickname);
        if (balances == null) {
            logger.warning("Cannot find balance for player " + nickname + " and currency " + currency + ": cache is empty.");
            return 0;
        }
        Optional<CurrencyData> possibleBalance = balances.stream().filter(balanceData -> balanceData.getCurrency().getName().equalsIgnoreCase(currency)).findFirst();
        if (possibleBalance.isEmpty()) {
            logger.warning("Cannot find balance for player " + nickname + " and currency " + currency + ": currency doesn't exist in player's cache.");
            return 0;
        }
        return possibleBalance.get().getAmount();
    }
}
