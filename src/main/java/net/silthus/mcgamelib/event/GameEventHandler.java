package net.silthus.mcgamelib.event;

import lombok.Getter;
import lombok.extern.java.Log;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.MCGameLib;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class GameEventHandler implements Listener {

    @Getter
    private final MCGameLib plugin;
    private final List<GameEventListener> listeners = new ArrayList<>();

    public GameEventHandler(MCGameLib plugin) {
        this.plugin = plugin;
    }

    public List<GameEventListener> getListeners() {
        return List.copyOf(listeners);
    }

    public void registerEvents(Game game, Listener listener) {
        registerGameEvents(game, listener);
        registerBukkitEvents(listener);
    }

    private void registerGameEvents(Game game, Listener listener) {
        createGameEventListeners(game, listener).forEach(this::registerEvent);
    }

    private void registerBukkitEvents(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    private void registerEvent(GameEventListener gameEventListener) {
        listeners.add(gameEventListener);
        Bukkit.getServer().getPluginManager().registerEvent(
                gameEventListener.eventClass(),
                gameEventListener.listener(),
                gameEventListener.annotation().priority(),
                (listener, event) -> callEvent(gameEventListener, listener, event),
                plugin,
                gameEventListener.annotation().ignoreCancelled()
        );
    }

    private void callEvent(GameEventListener gameListener, Listener listener, Event event) {
        try {
            if (isFiltered(gameListener, event)) return;
            gameListener.method().invoke(listener, event);
        } catch (Exception e) {
            log.warning("failed to call event " + gameListener + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isFiltered(GameEventListener listener, Event event) {
        if (listener.annotation().ignoreFilters()) return false;

        return isAnyFilterFiltering(listener, event);
    }

    private boolean isAnyFilterFiltering(GameEventListener listener, Event event) {
        for (GameEventFilter filter : listener.filters()) {
            if (!filter.test(listener, event))
                return true;
        }

        return false;
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
        GameEvent annotation = method.getAnnotation(GameEvent.class);
        return new GameEventListener(
                game,
                listener,
                (Class<? extends Event>) method.getParameterTypes()[0],
                method,
                annotation,
                getGameEventFilters(annotation)
        );
    }

    private List<? extends GameEventFilter> getGameEventFilters(GameEvent annotation) {
        return Arrays.stream(annotation.filters())
                .map(this::createFilterInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private GameEventFilter createFilterInstance(Class<? extends GameEventFilter> filterClass) {
        try {
            Constructor<? extends GameEventFilter> constructor = filterClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warning("unable to create instance of game event filter for: "
                    + filterClass.getCanonicalName() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasGameEventAnnotation(Method method) {
        return method.isAnnotationPresent(GameEvent.class);
    }

    private boolean hasEventParameter(Method method) {
        return method.getParameterCount() == 1
                && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    }
}
