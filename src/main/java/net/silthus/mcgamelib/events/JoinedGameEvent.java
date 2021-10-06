package net.silthus.mcgamelib.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import net.silthus.mcgamelib.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Value
@EqualsAndHashCode(callSuper = true)
public class JoinedGameEvent extends Event {

    Game game;
    Player player;

    public JoinedGameEvent(Game game, Player player) {

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
