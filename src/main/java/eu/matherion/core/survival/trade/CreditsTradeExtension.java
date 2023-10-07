package eu.matherion.core.survival.trade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.Zrips.TradeMe.Containers.*;
import me.Zrips.TradeMe.Locale.LC;
import me.Zrips.TradeMe.TradeMe;
import me.Zrips.TradeMe.Util;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreditsTradeExtension implements TradeModeInterface {

    private final TradeMe tradeInstance;
    private final String name;
    private final List<ItemStack> amountButtons = Lists.newArrayList();
    private final OfferButtons offerButtons = new OfferButtons();
    private Amounts amounts = new Amounts(1, 10, 100, 1000);
    private ItemStack offeredTradeButton = CMIMaterial.IRON_INGOT.newItemStack();

    public CreditsTradeExtension(TradeMe tradeInstance, String name) {
        this.tradeInstance = tradeInstance;
        this.name = name;
    }

    @Override
    public CMIGui Buttons(TradeOffer trade, CMIGui gui, int slot) {
        Util util = tradeInstance.getUtil();
        String firstBalance = util.TrA((long) 1); // @TODO: load credits (FridayPlayer.get(trade.getP1().getName()).loadSqlIntValue("credits") old
        String firstOffer = util.TrA(trade.getOffer(name));

        ItemStack ob = trade.getOffer(name) == 0 ? offerButtons.getOfferOff() : offerButtons.getOfferOn();
        String taxes = util.GetTaxesString(name, trade.getOffer(name));

        String mid = "";
        if (trade.getButtonList().size() > 4)
            mid = "\n" + tradeInstance.getMessage("MiddleMouse");
        if (trade.Size == TradeSize.REGULAR)
            gui.updateButton(new CMIGuiButton(slot, util.makeSlotItem(ob, tradeInstance.getMessage(name, "ToggleButton.Name"),
                    tradeInstance.getMessageListAsString(name, "ToggleButton.Lore",
                            "[amount]", util.TrA(trade.getOffer(name)),
                            "[taxes]", taxes) + mid)) {
                @Override
                public void click(GUIManager.GUIClickType click) {
                    trade.toogleMode(name, click, slot);
                }
            });

        if (trade.getAction().equals(name)) {
            String lore = tradeInstance.getMessageListAsString(name, "Button.Lore",
                    "[balance]", firstBalance,
                    "[offer]", firstOffer,
                    "[taxes]", taxes);
            for (int i = 45; i < 49; i++) {
                gui.updateButton(new CMIGuiButton(i, util.makeSlotItem(amountButtons.get(i - 45),
                        tradeInstance.getMessage(name, "Button.Name", "[amount]", util.TrA(amounts.getAmount(i - 45))), lore)) {
                    @Override
                    public void click(GUIManager.GUIClickType click) {
                        trade.amountClick(name, click, this.getSlot() - 45, slot);
                    }
                });
            }
        }

        return gui;
    }

    @Override
    public void Change(TradeOffer trade, int slot, GUIManager.GUIClickType button) {
        Double amount = amounts.getAmount(slot);
        double playerCredits = 1; // @TODO: load credits 1
        double targetCredits = 2; // @TODO: load credits 2
        double OfferedCredits = trade.getOffer(name);

        Util util = tradeInstance.getUtil();

        if (button.isShiftClick()) amount *= 10;

        if (button.isLeftClick()) {
            if (tradeInstance.EssPresent && OfferedCredits + amount + targetCredits >= 10000000000000D) {
                amount = 10000000000000D - OfferedCredits - targetCredits;
                trade.getP1().sendMessage(tradeInstance.getMsg(LC.info_prefix) + tradeInstance.getMessage(name, "hardLimit", "[playername]", trade.getP2Name()));
            }

            if (OfferedCredits + amount > playerCredits) {
                if (playerCredits < 0)
                    trade.setOffer(name, 0);
                else
                    trade.setOffer(name, Math.floor(playerCredits));
                trade.getP1().sendMessage(tradeInstance.getMsg(LC.info_prefix) + tradeInstance.getMessage(name, "Limit", "[amount]", util.TrA(trade.getOffer(name))));
            } else {
                trade.addOffer(name, amount);
            }
        }
        if (button.isRightClick())
            if (OfferedCredits - amount < 0) {
                trade.setOffer(name, 0);
            } else {
                trade.takeFromOffer(name, amount);
            }

        String msg = tradeInstance.getMessage(name, "ChangedOffer", "[playername]", trade.getP1Name(), "[amount]", util.TrA(trade.getOffer(name)));
        trade.getP2().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
        TradeMe.getInstance().getUtil().updateInventoryTitle(trade.getP2(), tradeInstance.getMessage(name, "ChangedOfferTitle", "[playername]", trade.getP1().getName(), "[amount]", trade.getOffer(name)), 1000L);
    }

    @Override
    public ItemStack getOfferedItem(TradeOffer trade) {
        Util util = tradeInstance.getUtil();
        if (trade.getOffer(name) > 0) {
            String taxes = util.GetTaxesString(name, trade.getOffer(name));
            return util.makeSlotItem(offeredTradeButton,
                    tradeInstance.getMessage(name, "OfferedButton.Name",
                            "[player]", trade.getP1().getName()),
                    tradeInstance.getMessageListAsString(name, "OfferedButton.Lore",
                            "[amount]", util.TrA(trade.getOffer(name)),
                            "[taxes]", taxes));
        }
        return null;
    }

    private boolean check(Player player, Player target, double offer1, double offer2) {
        int balance = 1; // @TODO: load credits
        if (balance < offer1) {
            player.sendMessage(tradeInstance.getMsg(LC.info_prefix) + tradeInstance.getMessage(name, "Error", "[playername]", player.getName()));
            target.sendMessage(tradeInstance.getMsg(LC.info_prefix) + tradeInstance.getMessage(name, "Error", "[playername]", player.getName()));
            return false;
        }

        balance = 2; // @TODO: load credits

        if (balance < offer2) {
            player.sendMessage(tradeInstance.getMsg(LC.info_prefix) + tradeInstance.getMessage(name, "Error", "[playername]", target.getName()));
            target.sendMessage(tradeInstance.getMsg(LC.info_prefix) + tradeInstance.getMessage(name, "Error", "[playername]", target.getName()));
            return false;
        }
        return true;
    }

    @Override
    public boolean isLegit(TradeMap tradeMap) {
        Player p1 = tradeMap.getP1Trade().getP1();
        Player p2 = tradeMap.getP2Trade().getP1();

        return check(p1, p2, tradeMap.getP1Trade().getOffer(name), tradeMap.getP2Trade().getOffer(name));
    }

    @Override
    public boolean finish(TradeOffer trade) {
        Player target = trade.getP2();
        Player source = trade.getP1();
        Util util = tradeInstance.getUtil();

        if (!check(source, target, trade.getOffer(name), 0D)) return false;
        if (trade.getOffer(name) <= 0.0D) return true;

        double amount = trade.getOffer(name);
        if (amount < 0) return false;
        if (source != null) {
            if (!tradeInstance.getEconomy().has(source, amount)) return false;
            boolean done = true; // @TODO: remove credits MatherionUtil.removeCredits(source.getName(), (int) amount);
            if (!done) return false;
        }

        double taxedAmount = util.CheckTaxes(name, amount);
        if (taxedAmount < 0) return false;
        trade.setOffer(name, taxedAmount);
        if (target != null) {
            int kreditytamount = (int) taxedAmount;
            int credits = 1; // @TODO: load credits
            //FridayPlayer.get(target.getName()).updateSqlValue("credits", credits + kreditytamount); // @TODO: update credits
            //  KredityAPI.addCredits(target.getName(), kreditytamount); ???
            target.sendMessage(tradeInstance.getMsg(LC.info_prefix) + tradeInstance.getMessage(name, "Got", "[amount]", util.TrA(trade.getOffer(name))));
        }
        return true;
    }

    @Override
    public String Switch(TradeOffer tradeOffer, GUIManager.GUIClickType guiClickType) {
        return null;
    }

    @Override
    public void setTrade(TradeOffer trade, int i) {
        trade.getButtonList().add(trade.getPosibleButtons().get(i));
    }


    @Override
    public void getResults(TradeOffer trade, TradeResults tradeResults) {
        Util util = tradeInstance.getUtil();
        if (trade.getOffer(name) > 0) {
            double amount = trade.getOffer(name);
            amount = amount - util.CheckFixedTaxes(name, amount);
            amount = amount - util.CheckPercentageTaxes(name, amount);
            tradeResults.add(name, amount);
        }
    }

    @Override
    public HashMap<String, Object> getLocale() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("Button.Name", "&2Nabídka kreditů: &6[amount]");
        map.put("Button.Lore", Arrays.asList(
                "&eKlikni levým pro zvýšení",
                "&eKlikni pravým pro odebrání",
                "&ePodrž shift pro zvýšení 10x",
                "&eNejvětší možný počet: &6[balance]",
                "&eAktuální nabídka peněz: &6[offer] [taxes]"));
        map.put("ToggleButton.Name", "&2Nastavit nabídku kreditů");
        map.put("ToggleButton.Lore", List.of("&eAktuální nabídka kreditů: &6[amount] [taxes]"));
        map.put("OfferedButton.Name", "&2Nabídka kreditů hráče [player]");
        map.put("OfferedButton.Lore", List.of("&eAktuální nabídka kreditů: &6[amount] [taxes]"));
        map.put("Error", "&e[playername] nemá dostatek kreditů!");
        map.put("Limit", "&eNemáš dostatek kreditů! Částka byla nastavanena na maximum: &6[amount]");
        map.put("hardLimit", "&6[playername] &enemůže mít více než 10,000,000,000,000 kreditů!");
        map.put("InLoanTarget", "&eTvoje nabízené kredity jsou přiliš malé pro &6[playername] &enabídni alespoň &6[amount]");
        map.put("InLoanYou", "&6[playername] &enabízený počet kreditů je příliš malý, aby se dostal hráč z dluhu!");
        map.put("Got", "&eObdržel jsi &6[amount] &ekreditů");
        map.put("CantWidraw", "&cNemůžeš vybírat kredity od hráče! ([playername])");
        map.put("ChangedOffer", "&6[playername] &ezměnil svoji nabídku kreditů na: &6[amount]");
        map.put("ChangedOfferTitle", "&8Nabídl &0[amount] &8kreditů");
        map.put("log", "&e[amount] &7Kredity");
        return map;
    }

    @Override
    public List<ItemStack> getAmountButtons() {
        amountButtons.add(CMIMaterial.IRON_NUGGET.newItemStack());
        amountButtons.add(CMIMaterial.IRON_INGOT.newItemStack());
        amountButtons.add(CMIMaterial.IRON_BLOCK.newItemStack());
        amountButtons.add(CMIMaterial.BEACON.newItemStack());
        return amountButtons;
    }

    @Override
    public ItemStack getOfferedTradeButton() {
        return offeredTradeButton;
    }

    @Override
    public void setOfferedTradeButton(ItemStack itemStack) {
        offeredTradeButton = itemStack;
    }

    @Override
    public OfferButtons getOfferButtons() {
        offerButtons.addOfferOff(CMIMaterial.IRON_INGOT.newItemStack());
        offerButtons.addOfferOn(CMIMaterial.IRON_INGOT.newItemStack());
        return offerButtons;
    }

    @Override
    public void setAmounts(Amounts amounts) {
        this.amounts = amounts;
    }
}
