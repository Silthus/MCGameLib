package net.silthus.mcgamelib.game.rules;

import net.silthus.mcgamelib.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bukkit.GameRule.DISABLE_RAIDS;
import static org.bukkit.GameRule.SPAWN_RADIUS;

public class MinecraftGameRuleTests extends TestBase {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    void builder() {

        MinecraftGameRule rule = MinecraftGameRule.builder().build();
        assertThat(rule)
                .isNotNull()
                .isInstanceOf(GameRule.class);
    }

    @Test
    void builder_withGameRules() {

        MinecraftGameRule rule = MinecraftGameRule.builder()
                .rule(SPAWN_RADIUS, 10)
                .rule(DISABLE_RAIDS, true)
                .build();

        assertThat(rule.getRules())
                .hasSize(2)
                .containsKeys(
                        SPAWN_RADIUS,
                        DISABLE_RAIDS
                );
    }
}
