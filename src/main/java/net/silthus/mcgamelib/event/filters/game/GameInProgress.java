package net.silthus.mcgamelib.event.filters.game;

import lombok.NonNull;
import net.silthus.mcgamelib.event.GameEventListener;
import net.silthus.mcgamelib.event.filters.GameEventFilter;
import org.bukkit.event.Event;

public class GameInProgress implements GameEventFilter {
    @Override
    public boolean test(@NonNull GameEventListener listener, @NonNull Event event) {
        return listener.game().isInProgress();
    }
}
