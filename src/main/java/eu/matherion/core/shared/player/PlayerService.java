package eu.matherion.core.shared.player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.currency.Currency;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Service
public class PlayerService {

    private final Map<String, MatherionPlayer> players = Maps.newHashMap();

    public ImmutableMap<String, MatherionPlayer> getPlayers() {
        return ImmutableMap.copyOf(players);
    }

    public Optional<MatherionPlayer> getPlayer(String nickname) {
        return Optional.ofNullable(players.get(nickname));
    }

    protected void addPlayer(MatherionPlayer player) {
        players.put(player.getNickname(), player);
    }

    protected void removePlayer(MatherionPlayer player) {
        players.remove(player.getNickname());
    }

}
