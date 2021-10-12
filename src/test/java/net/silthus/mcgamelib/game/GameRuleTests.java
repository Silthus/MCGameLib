package net.silthus.mcgamelib.game;

import net.silthus.configmapper.ConfigOption;
import net.silthus.configmapper.ConfigurationException;
import net.silthus.mcgamelib.game.rules.GamemodeGameRule;
import org.bukkit.GameMode;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class GameRuleTests {

    @Test
    void load_withClassAndConfig() {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("gamemode", "adventure");

        GamemodeGameRule rule = GameRule.load(GamemodeGameRule.class, config);

        assertThat(rule)
                .isNotNull()
                .extracting(GamemodeGameRule::getGamemode)
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

    @Nested
    @DisplayName("loadRules(...)")
    class LoadRules {

        @Test
        void withEmptyList_returnsEmptyList() {

            List<GameRule> rules = GameRule.loadRules(new HashMap<>());
            assertThat(rules).isEmpty();
        }
    }

    private static class ErrorGameRule implements GameRule {

        @ConfigOption
        private String test = "test";

        private ErrorGameRule() {}
    }
}