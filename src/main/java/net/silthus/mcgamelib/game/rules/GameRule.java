package net.silthus.mcgamelib.game.rules;

import net.silthus.configmapper.bukkit.BukkitConfigMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public interface GameRule extends Listener {

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
}
