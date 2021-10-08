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

public class PlayerJoinedGameEventTests extends TestBase {

    private Game game;
    private EventListener listener;
    private ArgumentCaptor<PlayerJoinedGameEvent> event = ArgumentCaptor.forClass(PlayerJoinedGameEvent.class);

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
        PlayerJoinedGameEvent event1 = new PlayerJoinedGameEvent(game, player);
        PlayerJoinedGameEvent event2 = new PlayerJoinedGameEvent(game, player);

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
                        PlayerJoinedGameEvent::getGame,
                        PlayerJoinedGameEvent::getPlayer
                ).contains(
                        game,
                        player
                );
    }

    static class EventListener implements Listener {

        private Consumer<PlayerJoinedGameEvent> asserts = joinedGameEvent -> {};

        @EventHandler
        public void onJoinedGame(PlayerJoinedGameEvent event) {
            asserts.accept(event);
        }
    }
}
