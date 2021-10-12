package net.silthus.mcgamelib.game.rules;

import lombok.Getter;
import net.silthus.mcgamelib.game.GameRule;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MinecraftGameRule implements GameRule {

    private final Map<org.bukkit.GameRule<?>, Object> rules = new HashMap<>();

    public <TValue> MinecraftGameRule rule(org.bukkit.GameRule<TValue> gameRule, TValue value) {
        rules.put(gameRule, value);
        return this;
    }
}
