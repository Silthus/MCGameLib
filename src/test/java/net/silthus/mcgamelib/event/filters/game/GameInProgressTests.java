package net.silthus.mcgamelib.event.filters.game;

import net.silthus.mcgamelib.event.GameEvent;
import net.silthus.mcgamelib.event.filters.FilterTestBase;
import net.silthus.mcgamelib.event.filters.GameEventFilter;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameInProgressTests extends FilterTestBase {

    private GameInProgress filter;
    private TestListener eventListener;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        filter = new GameInProgress();
        eventListener = spy(new TestListener());
        game.registerEvents(eventListener);
    }

    @Test
    void create() {

        assertThat(filter)
                .isInstanceOf(GameEventFilter.class);
    }

    @Test
    void returnsTrue_ifGameIsInProgress() {

        game.start();

        assertThat(filter.test(listener, new PlayerJoinEvent(server.addPlayer(), "")))
                .isTrue();
        verify(eventListener).onJoin(any());
    }

    @Test
    void returnsFalse_ifGameNotStarted() {

        assertThat(filter.test(listener, new PlayerJoinEvent(server.addPlayer(), "")))
                .isFalse();
        verify(eventListener, never()).onJoin(any());
    }

    static class TestListener implements Listener {

        @GameEvent(filters = {GameInProgress.class})
        public void onJoin(PlayerJoinEvent event) {

        }
    }
}
