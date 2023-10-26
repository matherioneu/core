package eu.matherion.core.survival.level.event;

import eu.matherion.core.shared.player.MatherionPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class MatherionLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final MatherionPlayer matherionPlayer;
    private final int level;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
