package net.silthus.mcgamelib.game;

import lombok.*;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Value
@EqualsAndHashCode(of = {"name"})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfiguredGameRule<TRule extends GameRule> {

    String name;
    Class<TRule> ruleClass;
    Supplier<TRule> supplier;
    @With Consumer<TRule> values;

    public ConfiguredGameRule(Class<TRule> ruleClass) {
        this(ruleClass, null);
    }

    public ConfiguredGameRule(Class<TRule> ruleClass, Consumer<TRule> values) {
        this.name = GameRule.getName(ruleClass);
        this.ruleClass = ruleClass;
        this.supplier = GameRuleRegistry.instance().getSupplier(ruleClass);
        this.values = values;
    }

    public ConfiguredGameRule<TRule> withConfig(ConfigurationSection config) {
        Consumer<TRule> configuration = rule -> BukkitConfigMap.of(rule).with(config).apply();
        if (values == null) {
            return withValues(configuration);
        }
        return withValues(values.andThen(configuration));
    }
}
