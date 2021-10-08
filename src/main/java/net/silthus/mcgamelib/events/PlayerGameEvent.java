package net.silthus.mcgamelib.events;

import lombok.Getter;
import net.silthus.mcgamelib.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@Getter
public abstract class PlayerGameEvent extends Event {

    private final Game game;
    private final Player player;

    public PlayerGameEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }
}
