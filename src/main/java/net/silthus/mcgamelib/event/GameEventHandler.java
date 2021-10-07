package net.silthus.mcgamelib.event;

import lombok.Getter;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.MCGameLib;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameEventHandler implements Listener {

    @Getter
    private final MCGameLib plugin;
    private final List<GameEventListener> listeners = new ArrayList<>();
//    private final EventExecutor eventExecutor = (listener, event) -> callEvent(event);

    public GameEventHandler(MCGameLib plugin) {
        this.plugin = plugin;
    }

    public Collection<GameEventListener> getListeners() {
        return List.copyOf(listeners);
    }

    public void registerEvents(Game game, Listener listener) {
        for (GameEventListener gameEventListener : createGameEventListeners(game, listener)) {
            listeners.add(gameEventListener);
            Bukkit.getServer().getPluginManager().registerEvent(
                    gameEventListener.getEventClass(),
                    gameEventListener.getListener(),
                    gameEventListener.getAnnotation().priority(),
                    (l, event) -> callEvent(gameEventListener.getMethod(), l, event),
                    plugin,
                    gameEventListener.getAnnotation().ignoreCancelled()
            );
        }
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    private void callEvent(Method method, Listener l, Event event) {
        try {
            method.invoke(l, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private List<GameEventListener> createGameEventListeners(Game game, Listener listener) {
        return getGameEventMethods(listener)
                .map(method -> createGameEventListener(game, listener, method))
                .collect(Collectors.toList());
    }

    private Stream<Method> getGameEventMethods(Listener listener) {
        return Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(this::hasGameEventAnnotation)
                .filter(this::hasEventParameter);
    }

    @SuppressWarnings("unchecked")
    private GameEventListener createGameEventListener(Game game, Listener listener, Method method) {
        return new GameEventListener(game, listener, (Class<? extends Event>) method.getParameterTypes()[0], method, method.getAnnotation(GameEvent.class));
    }

    private boolean hasGameEventAnnotation(Method method) {
        return method.isAnnotationPresent(GameEvent.class);
    }

    private boolean hasEventParameter(Method method) {
        return method.getParameterCount() == 1
                && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    }
}
