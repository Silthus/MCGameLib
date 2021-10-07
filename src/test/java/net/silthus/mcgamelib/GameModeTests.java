package net.silthus.mcgamelib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
                .isZero();
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
}
