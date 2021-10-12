package net.silthus.mcgamelib;

import lombok.*;
import net.silthus.configmapper.ConfigOption;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import net.silthus.mcgamelib.game.GameRule;
import net.silthus.mcgamelib.game.GameRuleRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
    private Map<Class<? extends GameRule>, Consumer<? extends GameRule>> rules = new HashMap<>();

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

    private Map<Class<? extends GameRule>, Consumer<? extends GameRule>> applyConfigToRules(Map<Class<? extends GameRule>, Consumer<? extends GameRule>> rules, ConfigurationSection config) {
        Map<String, ConfigurationSection> ruleConfigs = mapConfigToRuleConfigMap(config);
        return rules.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> configureRule(ruleConfigs, entry)
                ));
    }

    private Consumer<? extends GameRule> configureRule(Map<String, ConfigurationSection> ruleConfigs, Map.Entry<Class<? extends GameRule>, Consumer<? extends GameRule>> entry) {
        return entry.getValue().andThen(gameRule -> {
            ConfigurationSection config = ruleConfigs.get(GameRule.getName(entry.getKey()));
            if (config == null) return;
            BukkitConfigMap.of(gameRule)
                    .with(config)
                    .apply();
        });
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
                rules$value = new HashMap<>();
            rules$value.put(ruleClass, rule);
            rules$set = true;
            return this;
        }

    }
}
