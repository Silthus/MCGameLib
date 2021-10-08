package net.silthus.mcgamelib.event.filters.players;

import lombok.NonNull;
import net.silthus.mcgamelib.event.GameEventListener;
import net.silthus.mcgamelib.event.filters.PlayerFilter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class Spectating implements PlayerFilter {

    @Override
    public boolean test(@NonNull GameEventListener listener, @NonNull Event event, @NonNull Player player) {
        return listener.game().isSpectating(player);
    }
}
