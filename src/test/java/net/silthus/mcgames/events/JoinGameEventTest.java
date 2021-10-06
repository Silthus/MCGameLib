package net.silthus.mcgames.events;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.mcgames.Game;
import net.silthus.mcgames.GameMode;
import net.silthus.mcgames.TestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JoinGameEventTest extends TestBase {

    @Test
    void equals_twoEvents_areNotEquals() {

        PlayerMock player = server.addPlayer();
        Game game = new Game(GameMode.builder().build());

        JoinGameEvent event1 = new JoinGameEvent(game, player);
        JoinGameEvent event2 = new JoinGameEvent(game, player);

        assertThat(event1.equals(event2)).isFalse();
    }
}