package net.silthus.mcgamelib.event.filters;

import lombok.NonNull;
import net.silthus.mcgamelib.event.EventHelper;
import net.silthus.mcgamelib.event.GameEventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface PlayerFilter extends GameEventFilter {

    @Override
    default boolean test(@NonNull GameEventListener listener, @NonNull Event event) {
        return EventHelper.extractPlayerFromEvent(event)
                .map(player -> test(listener, event, player))
                .orElse(true);
    }

    boolean test(@NonNull GameEventListener listener, @NonNull Event event, @NonNull Player player);
}
