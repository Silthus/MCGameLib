package net.silthus.mcgamelib.event;

import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.GameMode;
import net.silthus.mcgamelib.TestBase;
import net.silthus.mcgamelib.events.JoinedGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class GameEventHandlerTests extends TestBase {

    private GameEventHandler eventHandler;
    private Game game;
    private ArgumentCaptor<JoinedGameEvent> event = ArgumentCaptor.forClass(JoinedGameEvent.class);

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        eventHandler = new GameEventHandler(plugin);
        game = new Game(GameMode.builder().build());
    }

    @Test
    void create() {
        assertThat(eventHandler.getPlugin())
                .isEqualTo(plugin);
    }

    @Test
    void getListeners_isEmpty() {
        assertThat(eventHandler.getListeners())
                .isEmpty();
    }

    @Test
    void getListeners_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> eventHandler.getListeners().add(GameEventListener.empty()));
    }

    @Test
    void registerEvents_registersListener() {
        SingleEventListener listener = new SingleEventListener();
        eventHandler.registerEvents(game, listener);

        assertThat(eventHandler.getListeners())
                .hasSize(1)
                .first()
                .isInstanceOf(GameEventListener.class)
                .extracting(
                        GameEventListener::getListener,
                        GameEventListener::getGame,
                        GameEventListener::getEventClass
                ).contains(
                        listener,
                        game,
                        JoinedGameEvent.class
                );
    }

    @Test
    void eventIsFired_afterRegister() {

        SingleEventListener listener = spy(new SingleEventListener());
        eventHandler.registerEvents(game, listener);

        Bukkit.getPluginManager().callEvent(new JoinedGameEvent(game, server.addPlayer()));

        verify(listener).onJoinGameEvent(event.capture());
    }

    @Test
    void registerEvents_registersBukkitListener() {

        SingleEventListener listener = new SingleEventListener();
        eventHandler.registerEvents(game, listener);

        assertThat(HandlerList.getRegisteredListeners(plugin))
                .anyMatch(registeredListener -> registeredListener.getListener().equals(listener));
    }

    static class SingleEventListener implements Listener {

        @GameEvent
        public void onJoinGameEvent(JoinedGameEvent event) {

        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {

        }
    }
}
