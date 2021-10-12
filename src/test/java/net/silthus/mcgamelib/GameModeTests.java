package net.silthus.mcgamelib;

import net.silthus.mcgamelib.game.rules.GamemodeGameRule;
import net.silthus.mcgamelib.game.rules.MaxHealthGameRule;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.Guard;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ALL")
public class GameModeTests extends TestBase {

    private GameMode gameMode;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        gameMode = GameMode.builder()
                .identifier("test")
                .build();
    }

    @Test
    void create() {

        assertThatCode(() -> GameMode.builder().build())
                .doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void create_withoutIdentifier_usesRandomUUID() {

        GameMode gameMode = GameMode.builder().build();

        assertThatCode(() -> UUID.fromString(gameMode.getIdentifier()))
                .doesNotThrowAnyException();
    }

    @Test
    void createWithIdentifier_setsIdentifier() {

        assertThat(gameMode.getIdentifier())
                .isEqualTo("test");
    }

    @Test
    void identifier_null_usesRandomGuid() {

        GameMode gameMode = GameMode.builder()
                .identifier(null)
                .build();

        assertThat(gameMode.getIdentifier())
                .isNotNull()
                .matches(s -> UUID.fromString(s) != null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void idententifier_empty_usesRandomGuid(String id) {

        GameMode gameMode = GameMode.builder()
                .identifier(id)
                .build();

        assertThat(gameMode.getIdentifier())
                .isNotNull()
                .isNotEqualTo(id)
                .matches(s -> UUID.fromString(s) != null);
    }

    @Test
    void name_setsNameOfGameMode() {

        gameMode = GameMode.builder()
                .name("Test Game Mode")
                .build();

        assertThat(gameMode.getName()).isEqualTo("Test Game Mode");
    }

    @Test
    void name_notSet_defaultsToDefaultName() {

        assertThat(gameMode.getName())
                .isEqualTo(GameMode.DEFAULT_NAME);
    }

    @Test
    void name_nullName_setsDefaultName() {

        gameMode = GameMode.builder()
                .name(null)
                .build();

        assertThat(gameMode.getName()).isEqualTo(GameMode.DEFAULT_NAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void name_emptyName_setsDefaultName(String name) {

        gameMode = GameMode.builder()
                .name(name)
                .build();

        assertThat(gameMode.getName()).isEqualTo(GameMode.DEFAULT_NAME);
    }

    @Test
    void minPlayers_defaultsToZero() {

        assertThat(gameMode.getMinPlayers())
                .isEqualTo(-1);
    }

    @Test
    void setMinPlayers_updatesField() {

        gameMode = GameMode.builder()
                .minPlayers(2)
                .build();

        assertThat(gameMode.getMinPlayers()).isEqualTo(2);
    }

    @Test
    void minPlayers_cannotBeSetToBelowZero() {

        gameMode = GameMode.builder()
                .minPlayers(-5)
                .build();

        assertThat(gameMode.getMinPlayers()).isZero();
    }

    @Test
    void maxPlayers_defaultsToMinusOne() {

        assertThat(gameMode.getMaxPlayers())
                .isEqualTo(-1);
    }

    @Test
    void setMaxPlayers_updatesField() {

        gameMode = GameMode.builder()
                .maxPlayers(10)
                .build();

        assertThat(gameMode.getMaxPlayers()).isEqualTo(10);
    }

    @Test
    void setMaxPlayers_belowMinPlayers_setsMinPlayersToMaxPlayers() {

        gameMode = GameMode.builder()
                .minPlayers(4)
                .maxPlayers(2)
                .build();

        assertThat(gameMode.getMinPlayers()).isEqualTo(2);
        assertThat(gameMode.getMaxPlayers()).isEqualTo(2);
    }

    @Test
    void setMaxPlayers_toMinusOne_doesNotSetMinPlayers() {

        gameMode = GameMode.builder()
                .minPlayers(2)
                .maxPlayers(-1)
                .build();

        assertThat(gameMode.getMinPlayers()).isEqualTo(2);
        assertThat(gameMode.getMaxPlayers()).isEqualTo(-1);
    }

    @Test
    void settingMinPlayersAboveMaxPlayers_setsMaxPlayers() {

        gameMode = GameMode.builder()
                .maxPlayers(5)
                .minPlayers(10)
                .build();

        assertThat(gameMode.getMinPlayers()).isEqualTo(10);
        assertThat(gameMode.getMaxPlayers()).isEqualTo(10);
    }

    @Test
    void hasMaxPlayerLimit_isFalse_ifMaxPlayersIsMinusOne() {

        assertThat(gameMode.hasMaxPlayerLimit())
                .isFalse();
    }

    @Test
    void hasMaxPlayerLimit_isTrue_ifMaxPlayersIsGreaterThenZero() {

        gameMode = GameMode.builder()
                .maxPlayers(2)
                .build();

        assertThat(gameMode.hasMaxPlayerLimit()).isTrue();
    }

    @Test
    void equals_basedOnIdentifier() {

        GameMode gameMode1 = GameMode.builder().identifier("test").build();
        GameMode gameMode2 = GameMode.builder().identifier("test").build();
        GameMode gameMode3 = GameMode.builder().build();
        GameMode gameMode4 = GameMode.builder().build();

        assertThat(gameMode1.equals(gameMode2)).isTrue();
        assertThat(gameMode1.equals(gameMode3)).isFalse();
        assertThat(gameMode2.equals(gameMode3)).isFalse();
        assertThat(gameMode3.equals(gameMode4)).isFalse();
    }

    @Test
    void newGame_containsGameMode() {

        GameMode gameMode = GameMode.builder().build();

        Game game = gameMode.newGame();

        assertThat(game.getGameMode()).isEqualTo(gameMode);
    }

    @Test
    void fromConfig_createsGameMode_withConfig() {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("name", "GameMode Test");
        config.set("min_players", 5);
        config.createSection("rules", Map.of(
                "gamemode", "creative",
                "max_health", 30
        ));

        GameMode gameMode = GameMode.fromConfig(config);

        assertThat(gameMode)
                .isNotNull()
                .extracting(
                        GameMode::getName,
                        GameMode::getMinPlayers,
                        GameMode::getMaxPlayers
                ).contains(
                        "GameMode Test",
                        5,
                        -1
                );
        assertThat(gameMode.getRules())
                .hasSize(2)
                .containsKeys(
                        GamemodeGameRule.class,
                        MaxHealthGameRule.class
                );
    }

    @Test
    void withers_setProperties() {

        GameMode gameMode = GameMode.builder().build()
                .withIdentifier("test")
                .withName("Test GM")
                .withMinPlayers(2)
                .withMaxPlayers(3);

        assertThat(gameMode)
                .extracting(
                        GameMode::getIdentifier,
                        GameMode::getName,
                        GameMode::getMinPlayers,
                        GameMode::getMaxPlayers
                ).contains(
                        "test",
                        "Test GM",
                        2,
                        3
                );
    }

    @Nested
    @DisplayName("with rules")
    class WithRules {

        @Test
        void createGameMode_withOutRules_isEmpty() {

            assertThat(GameMode.builder().build().getRules())
                    .isEmpty();
        }

        @Test
        void createGameMode_withRules() {

            GameMode gameMode = GameMode.builder()
                    .rule(MaxHealthGameRule.class)
                    .build();

            assertThat(gameMode.getRules())
                    .hasSize(1)
                    .containsOnlyKeys(MaxHealthGameRule.class);
        }

        @Test
        void createGameMode_withRuleAndConfig() {

            Consumer<MaxHealthGameRule> ruleConfig = rule -> rule.setMaxHealth(30);
            GameMode gameMode = GameMode.builder()
                    .rule(MaxHealthGameRule.class, ruleConfig)
                    .build();

            assertThat(gameMode.getRules())
                    .hasSize(1)
                    .containsOnly(
                            entry(MaxHealthGameRule.class, ruleConfig)
                    );
        }

        @Test
        void withMultipleRules() {

            GameMode gameMode = GameMode.builder()
                    .rule(GamemodeGameRule.class, gameModeRule -> gameModeRule.setGamemode(org.bukkit.GameMode.ADVENTURE))
                    .rule(MaxHealthGameRule.class, maxHealthRule -> maxHealthRule.setMaxHealth(30d))
                    .build();

            assertThat(gameMode.getRules())
                    .hasSize(2)
                    .containsKeys(GamemodeGameRule.class, MaxHealthGameRule.class);
        }
    }

    @Nested
    @DisplayName("configure()")
    class Configure {

        @Test
        void configure_loadsDynamicRules() {

            MemoryConfiguration config = new MemoryConfiguration();
            config.createSection("rules", Map.of(
                    "gamemode", "creative",
                    "max_health", Map.of()
            ));
            GameMode gameMode = GameMode.builder().build().configure(config);

            assertThat(gameMode.getRules())
                    .hasSize(2);
        }

        @Test
        void configure_returnsSameConfiguration() {

            GameMode gameMode = GameMode.builder()
                    .minPlayers(5)
                    .maxPlayers(10)
                    .rule(MaxHealthGameRule.class, maxHealthGameRule -> maxHealthGameRule.setMaxHealth(100d))
                    .build();

            GameMode configured = gameMode.configure();

            assertThat(configured)
                    .extracting(
                            GameMode::getMinPlayers,
                            GameMode::getMaxPlayers
                    ).contains(5, 10);
        }

        @Test
        void configure_withConfig_updatesValues() {

            GameMode gameMode = GameMode.builder()
                    .name("Test")
                    .minPlayers(3)
                    .maxPlayers(5)
                    .rule(MaxHealthGameRule.class, maxHealthGameRule -> maxHealthGameRule.setMaxHealth(30d))
                    .build();

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("name", "My Game Mode");
            cfg.set("min_players", 10);
            cfg.set("max_players", 20);
            cfg.createSection("rules", Map.of(
                    "max_health", 100
            ));

            GameMode configured = gameMode.configure(cfg);
            assertThat(configured)
                    .isNotNull()
                    .extracting(
                            GameMode::getName,
                            GameMode::getMinPlayers,
                            GameMode::getMaxPlayers
                    ).contains(
                            "My Game Mode",
                            10,
                            20
                    );
            assertThat(configured.getRules()).hasSize(1).containsKeys(MaxHealthGameRule.class);

            Consumer<MaxHealthGameRule> consumer = (Consumer<MaxHealthGameRule>) configured.getRules().get(MaxHealthGameRule.class);
            MaxHealthGameRule rule = new MaxHealthGameRule();
            consumer.accept(rule);

            assertThat(rule.getMaxHealth()).isEqualTo(100d);
        }
    }
}
