package net.silthus.mcgames;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {
    protected ServerMock server;
    private MCGames plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MCGames.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
