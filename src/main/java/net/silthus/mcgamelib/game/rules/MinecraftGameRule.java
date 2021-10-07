package net.silthus.mcgamelib.game.rules;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class MinecraftGameRule implements GameRule {

    @Builder.Default
    private final Map<org.bukkit.GameRule<?>, Object> rules = new HashMap<>();

    public static class MinecraftGameRuleBuilder {

        public <TValue> MinecraftGameRuleBuilder rule(org.bukkit.GameRule<TValue> gameRule, TValue value) {
            if (!rules$set) {
                rules$value = new HashMap<>();
            }
            rules$value.put(gameRule, value);
            rules$set = true;
            return this;
        }
    }
}
