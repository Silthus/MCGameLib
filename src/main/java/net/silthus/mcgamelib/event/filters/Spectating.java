package net.silthus.mcgamelib.event.filters;

import net.silthus.mcgamelib.event.EventHelper;
import net.silthus.mcgamelib.event.GameEventFilter;
import net.silthus.mcgamelib.event.GameEventListener;
import org.bukkit.event.Event;

public class Spectating implements GameEventFilter {
    @Override
    public boolean test(GameEventListener listener, Event event) {

        return EventHelper.extractPlayerFromEvent(event)
                .map(player -> listener.game().isSpectating(player))
                .orElse(true);
    }
}
