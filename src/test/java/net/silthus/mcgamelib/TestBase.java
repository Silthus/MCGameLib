package net.silthus.mcgamelib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

    protected ServerMock server;
    protected MCGameLib plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MCGameLib.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
