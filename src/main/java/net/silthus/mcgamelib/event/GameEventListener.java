package net.silthus.mcgamelib.event;

import lombok.NonNull;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.event.filters.GameEventFilter;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public record GameEventListener(
        Game game,
        Listener listener,
        Class<? extends Event> eventClass,
        Method method,
        GameEvent annotation,
        @NonNull List<? extends GameEventFilter> filters
) {

    static GameEventListener empty() {
        return new GameEventListener(null, null, null, null, null, new ArrayList<>());
    }
}
