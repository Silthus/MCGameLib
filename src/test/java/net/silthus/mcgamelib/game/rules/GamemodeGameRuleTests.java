package net.silthus.mcgamelib.game.rules;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.GameMode;
import net.silthus.mcgamelib.TestBase;
import net.silthus.mcgamelib.events.PlayerJoinedGameEvent;
import net.silthus.mcgamelib.events.PlayerQuitGameEvent;
import net.silthus.mcgamelib.game.GameRule;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class GamemodeGameRuleTests extends TestBase {

    private GamemodeGameRule rule;
    private Game game;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        rule = new GamemodeGameRule();
        game = new Game(GameMode.builder().build());

        rule.setGamemode(org.bukkit.GameMode.CREATIVE);
    }

    @Test
    void create() {

        assertThat(rule)
                .isInstanceOf(GameRule.class);
    }

    @Test
    void onJoin_setsGameMode() {

        PlayerMock player = server.addPlayer();
        assertThat(player.getGameMode()).isEqualTo(org.bukkit.GameMode.SURVIVAL);

        rule.onPlayerJoin(new PlayerJoinedGameEvent(game, player));
        assertThat(player.getGameMode()).isEqualTo(org.bukkit.GameMode.CREATIVE);
    }

    @Test
    void onQuit_resetsGameModeToOldGameMode() {

        PlayerMock player = server.addPlayer();
        org.bukkit.GameMode oldGameMode = player.getGameMode();

        rule.onPlayerJoin(new PlayerJoinedGameEvent(game, player));
        assertThat(player.getGameMode()).isEqualTo(rule.getGamemode());
        rule.onPlayerQuit(new PlayerQuitGameEvent(game, player));
        assertThat(player.getGameMode()).isEqualTo(oldGameMode);
    }

    @Test
    void shouldNotUpdateGameMode_ifPlayerDidNotJoin() {

        PlayerMock player = server.addPlayer();
        org.bukkit.GameMode gameMode = player.getGameMode();

        assertThatCode(() -> rule.onPlayerQuit(new PlayerQuitGameEvent(game, player)))
                .doesNotThrowAnyException();
        assertThat(player.getGameMode()).isEqualTo(gameMode);
    }

    @Test
    void loadWithConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("gamemode", "creative");
        GamemodeGameRule rule = GamemodeGameRule.loadFrom(cfg);

        assertThat(rule.getGamemode())
                .isEqualTo(org.bukkit.GameMode.CREATIVE);
    }
}