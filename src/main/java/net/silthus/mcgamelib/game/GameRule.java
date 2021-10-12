package net.silthus.mcgamelib.game;

import lombok.NonNull;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import net.silthus.mcgamelib.game.rules.GamemodeGameRule;
import net.silthus.mcgamelib.game.rules.MaxHealthGameRule;
import net.silthus.mcgamelib.game.rules.MinecraftGameRule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.function.Consumer;

public interface GameRule extends Listener {

    Set<Class<? extends GameRule>> BUILTIN_GAME_RULE_TYPES = Set.of(
            GamemodeGameRule.class,
            MaxHealthGameRule.class,
            MinecraftGameRule.class
    );

    static <TRule extends GameRule> TRule load(Class<TRule> ruleClass, ConfigurationSection config) {
        return BukkitConfigMap.of(ruleClass)
                .with(config)
                .create();
    }

    static <TRule extends GameRule> TRule load(TRule rule, ConfigurationSection config) {
        return BukkitConfigMap.of(rule)
                .with(config)
                .apply();
    }

    static List<GameRule> loadRules(Map<Class<? extends GameRule>, Consumer<? extends GameRule>> ruleMap) {
        return new ArrayList<>();
    }

    static String getName(@NonNull Class<? extends GameRule> gameRuleClass) {
        return GameRuleUtil.getGameRuleName(gameRuleClass);
    }
}
