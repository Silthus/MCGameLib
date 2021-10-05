package net.silthus.mcgames;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

public class GameTests extends TestBase {

    private Game game;

    @BeforeEach
    public void setUp() {
        super.setUp();

        game = new Game();
    }

    @Test
    void newGame_state_isNotStarted() {

        assertThat(game.getState())
                .isNotNull()
                .isEqualTo(GameState.NOT_STARTED);
    }

    @Test
    void newGame_withNoTitle_returnsDefaultTitle() {

        assertThat(game.getTitle())
                .isEqualTo(Game.DEFAULT_GAME_TITLE);
    }

    @Test
    void setTitle_setsTitle() {

        game.setTitle("My Test Game");

        assertThat(game.getTitle()).isEqualTo("My Test Game");
    }

    @Test
    void setTitle_withNull_setsDefaultTitle() {

        game.setTitle(null);

        assertThat(game.getTitle()).isEqualTo(Game.DEFAULT_GAME_TITLE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "     "})
    void setTitle_withEmpty_setsDefaultTitle(String title) {

        game.setTitle(title);

        assertThat(game.getTitle()).isEqualTo(Game.DEFAULT_GAME_TITLE);
    }

    @Test
    void startGame_setsState_toStarted() {

        game.start();

        assertThat(game.getState()).isEqualTo(GameState.STARTED);
    }

    @Test
    void stopGame_setsState_toStopped() {

        game.stop();

        assertThat(game.getState()).isEqualTo(GameState.STOPPED);
    }

    @Test
    void getPlayers_isEmpty() {

        assertThat(game.getPlayers())
                .isEmpty();
    }

    @Test
    void getPlayers_isImmutable() {

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> game.getPlayers().add(server.addPlayer()));
    }

    @Test
    void join_addsPlayerToGame() {

        PlayerMock player = server.addPlayer();
        game.join(player);

        assertThat(game.getPlayers())
                .hasSize(1)
                .containsOnly(player);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void join_nullPlayer_throws() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> game.join(null));
    }

    @Test
    void join_samePlayerTwice_onlyAddsThePlayerOnce() {

        PlayerMock player = server.addPlayer();
        game.join(player);
        game.join(player);

        assertThat(game.getPlayers())
                .hasSize(1);
    }

    @Test
    void quit_removesPlayerFromGame() {

        PlayerMock player = server.addPlayer();
        game.join(player);

        game.quit(player);

        assertThat(game.getPlayers())
                .isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void quit_nullPlayer_throws() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> game.quit(null));
    }

    @Test
    void quit_nonExistingPlayer_doesNothing() {

        assertThat(game.getPlayers()).isEmpty();
        assertThatCode(() -> game.quit(server.addPlayer()))
                .doesNotThrowAnyException();
    }

    @Test
    void broadcast_sendsMessageToAllPlayers() {

        ArrayList<PlayerMock> players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PlayerMock player = server.addPlayer();
            game.join(player);
            players.add(player);
        }

        assertThat(game.getPlayers()).hasSize(10);

        game.broadcast("Hello Players!");

        assertThat(players)
                .allMatch(player -> ChatColor.stripColor(player.nextMessage())
                        .equals("[" + Game.DEFAULT_GAME_TITLE + "] Hello Players!"));
    }

    @Test
    void isPlaying_returnsFalse() {

        assertThat(game.isPlaying(server.addPlayer())).isFalse();
    }

    @Test
    void isPlaying_forJoinedPlayer_returnsTrue() {

        PlayerMock player = server.addPlayer();
        game.join(player);

        assertThat(game.isPlaying(player)).isTrue();
        assertThat(game.isPlaying(server.addPlayer())).isFalse();
    }

    @Test
    void getSpectators_isEmpty() {

        assertThat(game.getSpectators())
                .isEmpty();
    }

    @Test
    void getSpectators_isImmutable() {

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> game.getSpectators().add(server.addPlayer()));
    }

    @Test
    void getSpectators_joinPlayer_staysEmpty() {

        game.join(server.addPlayer());
        assertThat(game.getSpectators()).isEmpty();
    }

    @Test
    void spectate_addsPlayer_toSpectators() {

        PlayerMock player = server.addPlayer();
        game.spectate(player);

        assertThat(game.getSpectators())
                .hasSize(1)
                .containsOnly(player);
    }

    @Test
    void spectate_doesNotAddPlayerToPlayers() {

        game.spectate(server.addPlayer());

        assertThat(game.getPlayers()).isEmpty();
    }

    @Test
    void quit_removesSpectatingPlayer() {

        PlayerMock player = server.addPlayer();
        game.spectate(player);

        assertThat(game.getSpectators()).containsOnly(player);

        game.quit(player);
        assertThat(game.getSpectators()).isEmpty();
    }

    @Test
    void spectate_joinedPlayer_movesPlayerToSpectators() {

        PlayerMock player = server.addPlayer();

        game.join(player);
        assertThat(game.getPlayers()).hasSize(1);
        assertThat(game.getSpectators()).isEmpty();

        game.spectate(player);
        assertThat(game.getPlayers()).isEmpty();
        assertThat(game.getSpectators()).hasSize(1);
    }

    @Test
    void spectate_onlyAddsPlayerOnce() {

        PlayerMock player = server.addPlayer();
        game.spectate(player);
        game.spectate(player);

        assertThat(game.getSpectators()).hasSize(1);
    }

    @Test
    void isSpectating_returnsFalse() {

        assertThat(game.isSpectating(server.addPlayer())).isFalse();
    }

    @Test
    void isSpectating_withPlayer_returnsTrue() {

        PlayerMock player = server.addPlayer();
        game.spectate(player);

        assertThat(game.isSpectating(player)).isTrue();
        assertThat(game.isSpectating(server.addPlayer())).isFalse();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void spectate_withNullPlayer_throws() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> game.spectate(null));
    }

    @Test
    void getAllPlayers_returnsPlayersAndSpectators() {

        game.join(server.addPlayer());
        game.spectate(server.addPlayer());

        assertThat(game.getAllPlayers()).hasSize(2);
    }

    @Test
    void getAllPlayers_isImmutable() {

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> game.getAllPlayers().add(server.addPlayer()));
    }

    @Test
    void getScore_ofUnjoinedPlayer_defaultsToZero() {

        assertThat(game.getScore(server.addPlayer()))
                .isZero();
    }

    @Test
    void getScore_ofJoinedPlayer_defaultsToZero() {

        PlayerMock player = server.addPlayer();
        game.join(player);

        assertThat(game.getScore(player))
                .isZero();
    }

    @Test
    void setScore_updatesPlayerScore() {

        PlayerMock player = server.addPlayer();
        game.join(player);

        game.setScore(player, 100);

        assertThat(game.getScore(player)).isEqualTo(100);
    }

    @Test
    void setScore_ofUnjoinedPlayer_isNotUpdated() {

        PlayerMock player = server.addPlayer();
        game.setScore(player, 100);

        assertThat(game.getScore(player)).isZero();
    }
}
