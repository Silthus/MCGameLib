package net.silthus.mcgamelib.game;

import net.silthus.mcgamelib.GameException;
import net.silthus.mcgamelib.TestBase;
import net.silthus.mcgamelib.game.rules.GamemodeGameRule;
import net.silthus.mcgamelib.game.rules.MaxHealthGameRule;
import net.silthus.mcgamelib.game.rules.MinecraftGameRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Supplier;

import static net.silthus.mcgamelib.game.GameRule.BUILTIN_GAME_RULE_TYPES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class GameRuleRegistryTests extends TestBase {

    private GameRuleRegistry registry;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        registry = new GameRuleRegistry(BUILTIN_GAME_RULE_TYPES);
    }

    @Test
    void create_withoutDefaults_isEmpty() {

        GameRuleRegistry registry = new GameRuleRegistry();
        assertThat(registry.getRuleTypes()).isEmpty();
    }

    @Test
    void create_containsDefaultRules() {

        assertThat(registry.getRuleTypes())
                .containsOnly(
                        GamemodeGameRule.class,
                        MaxHealthGameRule.class,
                        MinecraftGameRule.class
                );
    }

    @Test
    void instance_isSingleton() {
        GameRuleRegistry registry = GameRuleRegistry.instance();
        assertThat(registry).isNotNull();
        assertThat(registry.equals(GameRuleRegistry.instance())).isTrue();
    }

    @Test
    void plugin_hasGameRuleRegistry() {

        assertThat(plugin.getGameRuleRegistry())
                .isNotNull()
                .isEqualTo(GameRuleRegistry.instance());
    }

    @Test
    void getRuleType_isImmutable() {

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> registry.getRuleTypes().add(MyCustomRule.class));
    }

    @Test
    void registerType_addsType_toTypeList() {

        registry.registerType(MyCustomRule.class);

        assertThat(registry.getRuleTypes())
                .contains(MyCustomRule.class);
    }

    @Test
    void registerType_doesNotAddDuplicates() {

        registry.registerType(MaxHealthGameRule.class);
        assertThat(registry.getRuleTypes())
                .containsOnlyOnce(MaxHealthGameRule.class);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void registerType_withNull_throws() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> registry.registerType(null));
    }

    @Test
    void registerType_duplicateName_ignoresNewType() {

        registry.registerType(MaxHealthRule.class);
        assertThat(registry.getRuleTypes())
                .doesNotContain(MaxHealthRule.class);
        assertThat(registry.getByName("max_health"))
                .isPresent().get()
                .matches(MaxHealthGameRule.class::isAssignableFrom);
    }

    @Test
    void registerType_withAnnotation_usesAnnotation() {

        registry.registerType(AnnotatatedRule.class);
        assertThat(registry.getRuleTypes())
                .contains(AnnotatatedRule.class);
        assertThat(registry.getByName("my-rule"))
                .isPresent().get()
                .matches(AnnotatatedRule.class::isAssignableFrom);
    }

    @Test
    void clear_removesAllDefault_rules() {

        registry.clear();

        assertThat(registry.getRuleTypes()).isEmpty();
    }

    @Test
    void getByName_returnsRuleByName() {

        Optional<Class<? extends GameRule>> rule = registry.getByName("max_health");
        assertThat(rule)
                .isPresent()
                .get()
                .matches(MaxHealthGameRule.class::isAssignableFrom);
    }

    @Test
    void getByName_withNullName_returnsEmptyOptional() {

        assertThat(registry.getByName(null))
                .isEmpty();
    }

    @Test
    void registerType_withSupplier() {

        Supplier<MyCustomRule> supplier = spy(new Supplier<MyCustomRule>() {
            @Override
            public MyCustomRule get() {
                return new MyCustomRule();
            }
        });
        registry.registerType(MyCustomRule.class, supplier);
        MyCustomRule rule = registry.create(MyCustomRule.class);

        assertThat(rule).isNotNull();
        verify(supplier).get();
    }

    @Test
    void registerType_withoutSupplier_usesDefaultSupplier() {

        registry.registerType(MyCustomRule.class);

        MyCustomRule rule = registry.create(MyCustomRule.class);
        assertThat(rule).isNotNull();
    }

    @Test
    void registerType_create_throwsGameException() {

        registry.registerType(MyErrorRule.class);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> registry.create(MyErrorRule.class))
                .withCauseInstanceOf(InvocationTargetException.class);
    }

    static class MyCustomRule implements GameRule {}

    static class MaxHealthRule implements GameRule {}

    @RuleName("my-rule")
    static class AnnotatatedRule implements GameRule {}

    static class MyErrorRule implements GameRule {
        public MyErrorRule() {
            throw new NullPointerException();
        }
    }
}
