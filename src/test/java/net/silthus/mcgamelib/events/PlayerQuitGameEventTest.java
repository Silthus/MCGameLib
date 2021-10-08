package net.silthus.mcgamelib.events;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.GameMode;
import net.silthus.mcgamelib.TestBase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class PlayerQuitGameEventTest extends TestBase {

    private Game game;
    private EventListener listener;
    private ArgumentCaptor<PlayerQuitGameEvent> onQuitGame = ArgumentCaptor.forClass(PlayerQuitGameEvent.class);

    @BeforeEach
    public void setUp() {
        super.setUp();

        game = new Game(GameMode.builder().build());
        listener = spy(new EventListener());
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Test
    void equals_twoEvents_areNotEquals() {

        PlayerMock player = server.addPlayer();
        Game game = new Game(GameMode.builder().build());

        PlayerQuitGameEvent event1 = new PlayerQuitGameEvent(game, player);
        PlayerQuitGameEvent event2 = new PlayerQuitGameEvent(game, player);

        assertThat(event1.equals(event2)).isFalse();
    }

    @Test
    void quitGameEvent_isFired() {

        PlayerMock player = server.addPlayer();
        game.join(player);
        game.quit(player);

        verify(listener).onQuitgame(onQuitGame.capture());
        PlayerQuitGameEvent quitGameEvent = onQuitGame.getValue();

        assertThat(quitGameEvent)
                .extracting(
                        PlayerQuitGameEvent::getGame,
                        PlayerQuitGameEvent::getPlayer
                ).contains(
                        game,
                        player
                );
    }

    static class EventListener implements Listener {

        @EventHandler
        public void onQuitgame(PlayerQuitGameEvent event) {

        }
    }
}