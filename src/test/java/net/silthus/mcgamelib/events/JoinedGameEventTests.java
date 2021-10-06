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

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class JoinedGameEventTests extends TestBase {

    private Game game;
    private EventListener listener;
    private ArgumentCaptor<JoinedGameEvent> event = ArgumentCaptor.forClass(JoinedGameEvent.class);

    @BeforeEach
    public void setUp() {
        super.setUp();

        game = new Game(GameMode.builder().build());
        listener = spy(new EventListener());
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Test
    void equals_isNotSame() {

        PlayerMock player = server.addPlayer();
        JoinedGameEvent event1 = new JoinedGameEvent(game, player);
        JoinedGameEvent event2 = new JoinedGameEvent(game, player);

        assertThat(event1.equals(event2)).isFalse();
    }

    @Test
    void joinedGame_isFired_afterJoin() {

        PlayerMock player = server.addPlayer();
        listener.asserts = joinedGameEvent -> assertThat(game.getPlayers()).containsOnly(player);

        game.join(player);

        verify(listener).onJoinedGame(event.capture());
        assertThat(event.getValue())
                .extracting(
                        JoinedGameEvent::getGame,
                        JoinedGameEvent::getPlayer
                ).contains(
                        game,
                        player
                );
    }

    static class EventListener implements Listener {

        private Consumer<JoinedGameEvent> asserts = joinedGameEvent -> {};

        @EventHandler
        public void onJoinedGame(JoinedGameEvent event) {
            asserts.accept(event);
        }
    }
}
