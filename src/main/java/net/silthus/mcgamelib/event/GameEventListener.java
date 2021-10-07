package net.silthus.mcgamelib.event;

import lombok.Value;
import net.silthus.mcgamelib.Game;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

@Value
public class GameEventListener {

    static GameEventListener empty() {
        return new GameEventListener(null, null, null, null, null);
    }

    Game game;
    Listener listener;
    Class<? extends Event> eventClass;
    Method method;
    GameEvent annotation;
}
