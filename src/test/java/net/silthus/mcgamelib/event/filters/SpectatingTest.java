package net.silthus.mcgamelib.event.filters;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpectatingTest extends FilterTest {

    private Spectating filter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        filter = new Spectating();
    }

    @Test
    void shouldReturnTrue_ifPlayerIsNull() {

        boolean result = filter.test(listener, new ServerLoadEvent(ServerLoadEvent.LoadType.RELOAD));

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_ifPlayerIsNotSpectating() {

        game.join(player);
        boolean test = filter.test(listener, new PlayerJoinEvent(player, ""));

        assertThat(test).isFalse();
    }

    @Test
    void shouldReturnTrue_ifPlayerIsSpectating() {

        game.spectate(player);
        boolean test = filter.test(listener, new PlayerJoinEvent(player, ""));

        assertThat(test).isTrue();
    }
}