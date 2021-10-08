package net.silthus.mcgamelib.event.filters;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.GameMode;
import net.silthus.mcgamelib.TestBase;
import net.silthus.mcgamelib.event.GameEventListener;
import net.silthus.mcgamelib.events.PlayerJoinedGameEvent;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public abstract class FilterTestBase extends TestBase {
    protected Game game;
    protected GameEventListener listener;
    protected PlayerMock player;
    protected PlayerJoinedGameEvent event;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        game = new Game(GameMode.builder().build());
        listener = new GameEventListener(game, null, null, null, null, new ArrayList<>());
        player = server.addPlayer();
        event = new PlayerJoinedGameEvent(game, player);
    }
}
