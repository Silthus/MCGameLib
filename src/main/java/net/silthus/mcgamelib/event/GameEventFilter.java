package net.silthus.mcgamelib.event;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GameEventFilter {

    boolean test(GameEventListener listener, Event event);
}
