package net.silthus.mcgamelib;

import net.silthus.mcgamelib.event.GameEventHandler;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MCGameLibTests extends TestBase {

    @Test
    void create() {

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(MCGameLib::new);
    }

    @Test
    void onEnable_setsPluginInstanceVariable() {

        assertThat(MCGameLib.instance())
                .isNotNull()
                .isEqualTo(plugin);
    }

    @Test
    void onEnable_creates_ServiceInstances() {

        assertThat(plugin)
                .extracting(MCGameLib::getGameEventHandler)
                .isNotNull();
    }

    @Test
    void registerEvents_delegatesTo_GameEventHandler() {

        GameEventHandler eventHandler = mock(GameEventHandler.class);
        plugin.setGameEventHandler(eventHandler);

        Game game = new Game(GameMode.builder().build());
        Listener listener = new Listener() {};
        plugin.registerEvents(game, listener);

        verify(eventHandler).registerEvents(game, listener);
    }
}
