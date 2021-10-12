package net.silthus.mcgamelib.game;

import lombok.NonNull;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameRuleUtil {

    public static String getGameRuleName(@NonNull Class<? extends GameRule> gameRuleClass) {
        String regex = "([A-Za-z])([A-Z0-9]+)";
        String replacement = "$1_$2";
        return gameRuleClass.getSimpleName()
                .replaceAll("GameRule$", "")
                .replaceAll("Rule$", "")
                .replaceAll(regex, replacement).toLowerCase();
    }
}
