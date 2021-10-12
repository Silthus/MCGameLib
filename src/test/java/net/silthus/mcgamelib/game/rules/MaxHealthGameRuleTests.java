package net.silthus.mcgamelib.game.rules;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.GameMode;
import net.silthus.mcgamelib.TestBase;
import net.silthus.mcgamelib.events.PlayerJoinedGameEvent;
import net.silthus.mcgamelib.events.PlayerQuitGameEvent;
import net.silthus.mcgamelib.game.GameRule;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class MaxHealthGameRuleTests extends TestBase {

    private MaxHealthGameRule rule;
    private Game game;
    private PlayerMock player;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        rule = new MaxHealthGameRule();
        game = new Game(GameMode.builder().build());
        player = server.addPlayer();
    }

    @Test
    void create() {

        assertThat(rule)
                .isInstanceOf(GameRule.class);
    }

    @Test
    void onJoin_updatesPlayerHealth() {

        rule.setMaxHealth(30);

        joinGame(player);

        assertHealth(player, 30d, 30d);
    }

    @Test
    void onQuit_resetsMaxHealth() {

        rule.setMaxHealth(30d);

        joinAndQuitGame(player);

        assertHealth(player, 20d, 20d);
    }

    @Test
    void onQuit_retainsPreviousMaxHealth() {

        rule.setMaxHealth(25);
        setMaxHealth(10d);

        joinAndQuitGame(player);

        assertHealth(player, 10d, 10d);
    }

    @Test
    void currentHealth_isUpdatedRelative() {

        rule.setMaxHealth(40);
        player.setHealth(10d);

        joinGame(player);

        assertHealth(player, 40d, 20d);
    }

    @Test
    void resetsHealth_toRelativePreviousHealth() {

        rule.setMaxHealth(100d);
        player.setHealth(10d);

        joinAndQuitGame(player);

        assertHealth(player, 20d, 10d);
    }

    @Test
    void resetsHealth_toPercentage_afterDamage() {

        rule.setMaxHealth(100d);
        player.setHealth(10d);

        joinGame(player);
        player.damage(10d);
        quitGame(player);

        assertHealth(player, 20d, 8d);
    }

    @Test
    void loadWithConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("max_health", 100d);
        MaxHealthGameRule rule = GameRule.load(MaxHealthGameRule.class, cfg);

        assertThat(rule.getMaxHealth())
                .isEqualTo(100d);
    }

    private void setMaxHealth(double maxHealth) {
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(maxHealth);
    }

    private void assertHealth(Player player, double maxHealth, double health) {
        assertThat(player)
                .extracting(
                        Player::getMaxHealth,
                        Player::getHealth
                ).contains(
                        maxHealth,
                        health
                );
    }

    private void joinAndQuitGame(Player player) {
        joinGame(player);
        quitGame(player);
    }

    private void joinGame(Player player) {
        rule.onJoin(new PlayerJoinedGameEvent(game, player));
    }

    private void quitGame(Player player) {
        rule.onQuit(new PlayerQuitGameEvent(game, player));
    }
}
