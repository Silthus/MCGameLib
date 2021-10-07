package net.silthus.mcgamelib;

import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MCGameLibTests extends TestBase {

    @Test
    void create() {

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(MCGameLib::new);
    }

    @Test
    public void shouldFirePlayerJoinEvent() {

        server.addPlayer();

        server.getPluginManager().assertEventFired(PlayerJoinEvent.class);
    }

}
