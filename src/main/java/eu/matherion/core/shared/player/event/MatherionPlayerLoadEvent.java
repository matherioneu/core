package eu.matherion.core.shared.player.event;

import cz.maku.mommons.player.event.CloudPlayerLoadEvent;
import eu.matherion.core.shared.player.MatherionPlayer;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatherionPlayerLoadEvent extends CloudPlayerLoadEvent {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final MatherionPlayer matherionPlayer;

    public MatherionPlayerLoadEvent(MatherionPlayer matherionPlayer) {
        super(matherionPlayer.bukkit(), matherionPlayer);
        this.matherionPlayer = matherionPlayer;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
}
