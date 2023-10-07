package eu.matherion.core.survival.trade;

import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;
import me.Zrips.TradeMe.Containers.AmountClickAction;
import me.Zrips.TradeMe.Containers.TradeAction;
import me.Zrips.TradeMe.TradeMe;
import org.bukkit.Bukkit;

public class TradeDependency extends CoreDependency<TradeMe> {

    public final static String CREDITS_EXTENSION_NAME = "Kredity";
    private final CoreApplication coreApplication;

    public TradeDependency(CoreApplication coreApplication) {
        super("trade", coreApplication);
        this.coreApplication = coreApplication;
    }

    @Override
    public TradeMe construct(CoreApplication coreApplication) {
        return TradeMe.getInstance();
    }

    @Override
    public void init() {
        TradeMe tradeMe = get();
        TradeAction action = new TradeAction(CREDITS_EXTENSION_NAME, AmountClickAction.Amounts, false);
        CreditsTradeExtension creditsTradeExtension = new CreditsTradeExtension(tradeMe, CREDITS_EXTENSION_NAME);
        tradeMe.addNewTradeMode(action, creditsTradeExtension);
        tradeMe.getConfigManager().reload();
        coreApplication.getLogger().info("TradeMe extension for credits has been registered.");
    }
}
