package net.silthus.mcgamelib.game.rules;

import net.silthus.configmapper.ConfigOption;
import net.silthus.configmapper.ConfigurationException;
import org.bukkit.GameMode;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class GameRuleTests {

    @Test
    void load_withClassAndConfig() {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("gamemode", "adventure");

        GameModeRule rule = GameRule.load(GameModeRule.class, config);

        assertThat(rule)
                .isNotNull()
                .extracting(GameModeRule::getGamemode)
                .isEqualTo(GameMode.ADVENTURE);
    }

    @Test
    void throws_ifInstanceCannotBeCreated() {

        assertThatExceptionOfType(ConfigurationException.class)
                .isThrownBy(() -> GameRule.load(ErrorGameRule.class, new MemoryConfiguration()));
    }

    @Test
    void doesNotThrow_ifInstanceIsProvided() {
        MemoryConfiguration config = new MemoryConfiguration();
        config.set("test", "foobar");
        ErrorGameRule rule = GameRule.load(new ErrorGameRule(), config);
        assertThat(rule).isNotNull()
                .extracting(errorGameRule -> errorGameRule.test)
                .isEqualTo("foobar");
    }

    private static class ErrorGameRule implements GameRule {

        @ConfigOption
        private String test = "test";

        private ErrorGameRule() {}
    }
}