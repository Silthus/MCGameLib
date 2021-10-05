package net.silthus.mcgames;

import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MCGamesTests extends TestBase {

    @Test
    void create() {

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(MCGames::new);
    }

    @Test
    public void shouldFirePlayerJoinEvent() {

        server.addPlayer();

        server.getPluginManager().assertEventFired(PlayerJoinEvent.class);
    }

}
