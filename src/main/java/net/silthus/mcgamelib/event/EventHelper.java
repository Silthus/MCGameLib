package net.silthus.mcgamelib.event;

import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Log
public class EventHelper {

    public static Optional<Player> extractPlayerFromEvent(Event event) {
        if (event instanceof PlayerEvent)
            return Optional.of(((PlayerEvent) event).getPlayer());
        else
            return extractPlayerFromClass(event);
    }

    private static Optional<Player> extractPlayerFromClass(Event event) {
        return Arrays.stream(event.getClass().getMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(EventHelper::returnTypeIsPlayer)
                .findFirst()
                .map(method -> {
                    try {
                        return (Player) method.invoke(event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.warning("unable to figure out player for game event \""
                                + event.getClass().getCanonicalName() + "::" + method.getName()
                                + "\": " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    private static boolean returnTypeIsPlayer(Method method) {
        return Player.class.isAssignableFrom(method.getReturnType());
    }

}
