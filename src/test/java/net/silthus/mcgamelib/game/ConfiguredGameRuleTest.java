package net.silthus.mcgamelib.game;

import net.silthus.mcgamelib.game.rules.MaxHealthGameRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguredGameRuleTest {

    @Test
    void withNoValues_createsValues() {

        ConfiguredGameRule<MaxHealthGameRule> rule = new ConfiguredGameRule<>(MaxHealthGameRule.class);
    }
}