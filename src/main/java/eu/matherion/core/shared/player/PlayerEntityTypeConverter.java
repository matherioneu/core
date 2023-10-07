package eu.matherion.core.shared.player;

import cz.maku.mommons.ef.converter.TypeConverter;
import cz.maku.mommons.worker.WorkerReceiver;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.currency.Currency;
import eu.matherion.core.shared.currency.CurrencyService;

public class PlayerEntityTypeConverter implements TypeConverter<MatherionPlayer, String> {

    @Override
    public String convertToColumn(MatherionPlayer matherionPlayer) {
        return matherionPlayer.getNickname();
    }

    @Override
    public MatherionPlayer convertToEntityField(String nickname) {
        PlayerService service = WorkerReceiver.getService(CoreApplication.class, PlayerService.class);
        if (service == null) {
            CoreApplication.logger().severe("PlayerService is null!");
            return null;
        }
        MatherionPlayer player = service.getPlayers().get(nickname);
        if (player == null) {
            CoreApplication.logger().severe("Player " + nickname + " not found!");
            return null;
        }
        return player;
    }
}
