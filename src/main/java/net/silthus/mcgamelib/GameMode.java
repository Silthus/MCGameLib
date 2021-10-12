package net.silthus.mcgamelib;

import lombok.*;
import net.silthus.configmapper.ConfigOption;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import net.silthus.mcgamelib.game.ConfiguredGameRule;
import net.silthus.mcgamelib.game.GameRule;
import net.silthus.mcgamelib.game.GameRuleRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class GameMode {

    public static final String DEFAULT_NAME = "Unnamed Game Mode";

    public static GameMode fromConfig(ConfigurationSection config) {
        return GameMode.builder()
                .build()
                .configure(config);
    }

    @Builder.Default
    @ConfigOption("id")
    @With
    private String identifier = UUID.randomUUID().toString();
    @Builder.Default
    @ConfigOption
    @With
    private String name = DEFAULT_NAME;
    @Builder.Default
    @ConfigOption
    @With
    private int minPlayers = -1;
    @Builder.Default
    @ConfigOption
    @With
    private int maxPlayers = -1;

    @Builder.Default
    @With
    private Set<ConfiguredGameRule<?>> rules = new HashSet<>();

    public boolean hasMaxPlayerLimit() {
        return maxPlayers > 0;
    }

    public Game newGame() {
        return new Game(this);
    }

    public GameMode configure() {
        return this;
    }

    public GameMode configure(ConfigurationSection config) {
        return BukkitConfigMap.of(withRulesFromConfig(config))
                .with(config)
                .apply();
    }

    private GameMode withRulesFromConfig(ConfigurationSection config) {

        ConfigurationSection rules = config.getConfigurationSection("rules");
        if (rules == null) return this;
        GameModeBuilder builder = toBuilder();
        for (String rule : rules.getKeys(false)) {
            GameRuleRegistry.instance().getByName(rule)
                    .ifPresent(builder::rule);
        }
        return withRules(applyConfigToRules(builder.build().getRules(), rules));
    }

    private Set<ConfiguredGameRule<?>> applyConfigToRules(Collection<ConfiguredGameRule<?>> rules, ConfigurationSection config) {
        Map<String, ConfigurationSection> ruleConfigs = mapConfigToRuleConfigMap(config);
        return rules.stream()
                .map(configuredGameRule -> {
                    ConfigurationSection ruleConfig = ruleConfigs.get(configuredGameRule.getName());
                    return configureRule(configuredGameRule, ruleConfig);
                }).collect(Collectors.toSet());
    }

    private ConfiguredGameRule<?> configureRule(ConfiguredGameRule<?> rule, ConfigurationSection config) {
        if (config == null) return rule;
        return rule.withConfig(config);
    }

    private Map<String, ConfigurationSection> mapConfigToRuleConfigMap(ConfigurationSection config) {
        if (config == null) return new HashMap<>();
        return config.getKeys(false).stream()
                .collect(Collectors.toMap(
                        rule -> rule,
                        rule -> {
                            ConfigurationSection ruleConfig = config.getConfigurationSection(rule);
                            if (ruleConfig != null)
                                return ruleConfig;
                            MemoryConfiguration cfg = new MemoryConfiguration();
                            cfg.set(rule, config.get(rule));
                            return cfg;
                        }
                ));
    }

    public static class GameModeBuilder {

        public GameModeBuilder identifier(String identifier) {
            if (identifier == null || identifier.trim().equals(""))
                return this;

            this.identifier$value = identifier;
            this.identifier$set = true;
            return this;
        }

        public GameModeBuilder name(String name) {
            if (name == null || name.trim().equals(""))
                name = DEFAULT_NAME;

            this.name$value = name;
            this.name$set = true;
            return this;
        }

        public GameModeBuilder minPlayers(int minPlayers) {
            if (minPlayers < 0)
                minPlayers = 0;
            if (minPlayers > maxPlayers$value)
                maxPlayers(minPlayers);

            this.minPlayers$value = minPlayers;
            this.minPlayers$set = true;
            return this;
        }

        public GameModeBuilder maxPlayers(int maxPlayers) {
            if (maxPlayers > 0 && maxPlayers < minPlayers$value)
                minPlayers(maxPlayers);

            this.maxPlayers$value = maxPlayers;
            this.maxPlayers$set = true;
            return this;
        }

        public GameModeBuilder rule(Class<? extends GameRule> rule) {
            return rule(rule, gameRule -> {
            });
        }

        public <TRule extends GameRule> GameModeBuilder rule(Class<TRule> ruleClass, Consumer<TRule> rule) {
            if (!rules$set)
                rules$value = new HashSet<>();
            rules$value.add(new ConfiguredGameRule<>(
                    ruleClass,
                    rule
            ));
            rules$set = true;
            return this;
        }

    }
}
