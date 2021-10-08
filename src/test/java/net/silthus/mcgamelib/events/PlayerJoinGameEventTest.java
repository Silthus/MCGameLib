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

class PlayerJoinGameEventTest extends TestBase {

    private Game game;
    private EventListener listener;
    private ArgumentCaptor<PlayerJoinGameEvent> onJoinGame = ArgumentCaptor.forClass(PlayerJoinGameEvent.class);

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

        PlayerJoinGameEvent event1 = new PlayerJoinGameEvent(game, player);
        PlayerJoinGameEvent event2 = new PlayerJoinGameEvent(game, player);

        assertThat(event1.equals(event2)).isFalse();
    }

    @Test
    void joinGameEvent_isFired() {

        PlayerMock player = server.addPlayer();
        game.join(player);

        verify(listener).onJoinGame(onJoinGame.capture());
        PlayerJoinGameEvent joinGameEvent = onJoinGame.getValue();

        assertThat(joinGameEvent)
                .extracting(
                        PlayerJoinGameEvent::getGame,
                        PlayerJoinGameEvent::getPlayer
                ).contains(
                        game,
                        player
                );
    }

    @Test
    void joinGameEvent_cancelDoesNotAddPlayer() {

        listener.cancelJoinGame = true;

        PlayerMock player = server.addPlayer();
        game.join(player);

        verify(listener).onJoinGame(onJoinGame.capture());
        assertThat(onJoinGame.getValue())
                .extracting(PlayerJoinGameEvent::isCancelled)
                .isEqualTo(true);

        assertThat(game.getPlayers()).isEmpty();
    }

    @Test
    void joinGameEvent_shouldFire_beforePlayerSizeCheck() {

        game = new Game(GameMode.builder().maxPlayers(1).build());
        PlayerMock player1 = server.addPlayer();
        game.join(player1);
        doAnswer(invocation -> {
            Game game = ((PlayerJoinGameEvent) invocation.getArgument(0)).getGame();
            game.quit(player1);
            return invocation;
        }).when(listener).onJoinGame(any());

        PlayerMock player2 = server.addPlayer();
        assertThatCode(() -> game.join(player2))
                .doesNotThrowAnyException();

        verify(listener, times(2)).onJoinGame(any());
    }

    static class EventListener implements Listener {

        boolean cancelJoinGame = false;

        @EventHandler
        public void onJoinGame(PlayerJoinGameEvent event) {
            event.setCancelled(cancelJoinGame);
        }
    }
}