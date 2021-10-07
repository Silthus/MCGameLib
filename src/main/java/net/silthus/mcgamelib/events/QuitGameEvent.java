package net.silthus.mcgamelib.events;

import lombok.Getter;
import lombok.NonNull;
import net.silthus.mcgamelib.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class QuitGameEvent extends PlayerGameEvent {

    public QuitGameEvent(Game game, Player player) {
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
