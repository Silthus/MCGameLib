package net.silthus.mcgamelib.event;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use the @{@code @GameEvent} annotation inside game objects
 * to listen to {@link org.bukkit.event.Event}s.
 * <p>It is an extension to the known {@link org.bukkit.event.EventHandler} annotation
 * and has an additional {@link #filters()} argument that takes a list
 * of filters that should be applied before calling the event.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameEvent {

    /**
     * Define the priority of the event.
     * <p>
     * First priority to the last priority executed:
     * <ol>
     * <li>LOWEST
     * <li>LOW
     * <li>NORMAL
     * <li>HIGH
     * <li>HIGHEST
     * <li>MONITOR
     * </ol>
     *
     * @return the priority
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Define if the handler ignores a cancelled event.
     * <p>
     * If ignoreCancelled is true and the event is cancelled, the method is
     * not called. Otherwise, the method is always called.
     *
     * @return whether cancelled events should be ignored
     */
    boolean ignoreCancelled() default false;

//    /**
//     * Provide a list of {@link EventFilter}s that should be applied
//     * before calling the event handler.
//     * <p>By default the event is only called for {@link net.silthus.mcgamelib.User}s
//     * and {@link org.bukkit.entity.Player}s that are currently playing the game.
//     * <p>You can overwrite this to for example only get notified of spectators
//     * by using the {@link net.silthus.mcgamelib.event.filters.SpectatorFilter}.
//     * <pre>{@code
//     * @GameEvent(filters = {SpectatorFilter.class})
//     * public void onPlayerDamage(PlayerDamageEvent event) {
//     *     event.setCancelled(true);
//     * }
//     * }</pre>
//     * @return a list of filters that should be applied to the event
//     */
//    Class<? extends EventFilter>[] filters() default {
//            PlayerFilter.class
//    };
}
