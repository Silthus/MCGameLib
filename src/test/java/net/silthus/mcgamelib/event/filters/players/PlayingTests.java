package net.silthus.mcgamelib.event.filters.players;

import net.silthus.mcgamelib.event.filters.FilterTestBase;
import org.bukkit.event.server.ServerLoadEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayingTests extends FilterTestBase {

    private Playing filter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        filter = new Playing();
    }

    @Test
    void returnsFalseIfPlayerIsNotInGame() {

        boolean result = filter.test(listener, event);

        assertThat(result).isFalse();
    }

    @Test
    void returnsTrueIfPlayerIsInGame() {

        game.join(player);

        boolean result = filter.test(listener, event);
        assertThat(result).isTrue();
    }

    @Test
    void returnsTrueIfPlayerIsNull() {

        boolean result = filter.test(listener, new ServerLoadEvent(ServerLoadEvent.LoadType.RELOAD));
        assertThat(result).isTrue();
    }
}