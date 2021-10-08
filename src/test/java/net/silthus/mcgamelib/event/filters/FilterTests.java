package net.silthus.mcgamelib.event.filters;

import net.silthus.mcgamelib.event.GameEvent;
import net.silthus.mcgamelib.event.GameEventListener;
import net.silthus.mcgamelib.event.filters.players.Spectating;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterTests extends FilterTestBase {

    @Test
    void should_apply_global_filtersFromAnnotation() {

        CustomListener listener = new CustomListener();
        plugin.registerEvents(game, listener);

        assertThat(plugin.getGameEventHandler().getListeners()
                .stream().filter(l -> l.listener().equals(listener))
                .findFirst())
                .isPresent().get()
                .extracting(GameEventListener::filters)
                .asList()
                .hasAtLeastOneElementOfType(Spectating.class);
    }

    @Filter({Spectating.class})
    static class CustomListener implements Listener {

        @GameEvent
        public void onPlayerJoin(PlayerJoinEvent event) {

        }
    }
}
