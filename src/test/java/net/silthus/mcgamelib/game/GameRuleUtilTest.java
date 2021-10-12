package net.silthus.mcgamelib.game;

import net.silthus.mcgamelib.game.rules.MaxHealthGameRule;
import org.assertj.core.api.Assertions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class GameRuleUtilTest {

    @Nested
    @DisplayName("getGameRuleName(...)")
    class GetName {

        @Test
        void getName_returnsNameOfRule_basedOnClassName() {

            Assertions.assertThat(GameRule.getName(MaxHealthGameRule.class))
                    .isEqualTo("max_health");
        }

        @Test
        void getName_withRuleNamedMyRule() {

            assertThat(GameRule.getName(MyRule.class))
                    .isEqualTo("my");
        }

        @Test
        void getName_withoutSuffix() {

            assertThat(GameRule.getName(WithoutSuffix.class))
                    .isEqualTo("without_suffix");
        }

        @Test
        void getName_withMultipleCamelCases() {

            assertThat(GameRule.getName(AReallyLongRuleName.class))
                    .isEqualTo("a_really_long_rule_name");
        }

        @Test
        void getName_withNumberInName() {

            assertThat(GameRule.getName(MyRule1.class))
                    .isEqualTo("my_rule_1");
        }

        public static class MyRule implements GameRule {}

        public static class WithoutSuffix implements GameRule {}

        public static class AReallyLongRuleName implements GameRule {}

        public static class MyRule1 implements GameRule {}
    }
}