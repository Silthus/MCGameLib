package net.silthus.mcgamelib.event.filters;

import lombok.NonNull;
import net.silthus.mcgamelib.event.GameEventListener;
import org.bukkit.event.Event;

@FunctionalInterface
public interface GameEventFilter {

    boolean test(@NonNull GameEventListener listener, @NonNull Event event);
}
