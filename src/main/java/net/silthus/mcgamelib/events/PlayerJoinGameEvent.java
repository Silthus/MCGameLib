package net.silthus.mcgamelib.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.mcgamelib.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class PlayerJoinGameEvent extends PlayerGameEvent implements Cancellable {

    private boolean cancelled;

    public PlayerJoinGameEvent(Game game, Player player) {
        super(game, player);
    }

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    @NonNull
    public HandlerList getHandlers() {
        return handlerList;
    }
}
