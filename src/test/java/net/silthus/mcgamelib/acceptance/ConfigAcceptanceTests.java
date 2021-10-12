package net.silthus.mcgamelib.acceptance;

import net.silthus.mcgamelib.GameMode;
import net.silthus.mcgamelib.TestBase;
import net.silthus.mcgamelib.game.ConfiguredGameRule;
import net.silthus.mcgamelib.game.rules.GamemodeGameRule;
import net.silthus.mcgamelib.game.rules.MaxHealthGameRule;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
public class ConfigAcceptanceTests extends TestBase {

    @Test
    void loadGameModeConfig() throws IOException, InvalidConfigurationException {

        YamlConfiguration config = new YamlConfiguration();
        config.load(new File("src/test/resources/gamemodes", "test-1.yml"));

        GameMode gameMode = GameMode.fromConfig(config);

        assertThat(gameMode)
                .isNotNull()
                .extracting(
                        GameMode::getIdentifier,
                        GameMode::getName,
                        GameMode::getMinPlayers,
                        GameMode::getMaxPlayers
                ).contains(
                        "test",
                        "Test 1",
                        2,
                        10
                );
        assertThat(gameMode.getRules().stream()
                .map(rule -> (Class) rule.getRuleClass()))
                .hasSize(2)
                .contains(
                        MaxHealthGameRule.class,
                        GamemodeGameRule.class
                );

//        List<GameRule> rules = GameRule.loadRules(gameMode.getRules());
//        GameModeGameRule gameModeRule = (GameModeGameRule) rules.get(0);
//        MaxHealthGameRule maxHealth = (MaxHealthGameRule) rules.get(1);
//
//        assertThat(gameModeRule.getGamemode())
//                .isEqualTo(org.bukkit.GameMode.ADVENTURE);
//        assertThat(maxHealth.getMaxHealth())
//                .isEqualTo(30);
    }
}
