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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuitGameEventTest extends TestBase {

    private Game game;
    private EventListener listener;
    private ArgumentCaptor<QuitGameEvent> onQuitGame = ArgumentCaptor.forClass(QuitGameEvent.class);

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

        QuitGameEvent event1 = new QuitGameEvent(game, player);
        QuitGameEvent event2 = new QuitGameEvent(game, player);

        assertThat(event1.equals(event2)).isFalse();
    }

    @Test
    void quitGameEvent_isFired() {

        PlayerMock player = server.addPlayer();
        game.join(player);
        game.quit(player);

        verify(listener).onQuitgame(onQuitGame.capture());
        QuitGameEvent quitGameEvent = onQuitGame.getValue();

        assertThat(quitGameEvent)
                .extracting(
                        QuitGameEvent::getGame,
                        QuitGameEvent::getPlayer
                ).contains(
                        game,
                        player
                );
    }

    static class EventListener implements Listener {

        @EventHandler
        public void onQuitgame(QuitGameEvent event) {

        }
    }
}