package net.silthus.mcgamelib.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.mcgamelib.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = true)
public class JoinGameEvent extends Event implements Cancellable {

    private final Game game;
    private final Player player;
    private boolean cancelled;

    public JoinGameEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    @NonNull
    public HandlerList getHandlers() {
        return handlerList;
    }
}
