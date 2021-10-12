package net.silthus.mcgamelib.game;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.silthus.mcgamelib.GameException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

@Log
public class GameRuleRegistry {

    private static GameRuleRegistry instance;

    public static GameRuleRegistry instance() {
        if (instance == null)
            instance = new GameRuleRegistry(GameRule.BUILTIN_GAME_RULE_TYPES);
        return instance;
    }

    private final Map<String, Class<? extends GameRule>> ruleTypes = new HashMap<>();
    private final Map<Class<? extends GameRule>, Supplier<? extends GameRule>> gameRuleSuppliers = new HashMap<>();

    public GameRuleRegistry() {
    }

    public GameRuleRegistry(Set<Class<? extends GameRule>> defaults) {
        defaults.forEach(this::registerType);
    }

    public Collection<Class<? extends GameRule>> getRuleTypes() {
        return List.copyOf(ruleTypes.values());
    }

    public void clear() {
        ruleTypes.clear();
    }

    public <TRule extends GameRule> void registerType(@NonNull Class<TRule> ruleClass) {
        registerType(ruleClass, () -> {
            try {
                Constructor<TRule> constructor = ruleClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new GameException("Unable to create game rule instance of " + ruleClass.getCanonicalName() + ": " + e.getMessage(), e);
            }
        });

    }

    public <TRule extends GameRule> void registerType(Class<TRule> ruleClass, Supplier<TRule> supplier) {
        String name = getRuleName(ruleClass);
        if (ruleTypes.containsKey(name)) {
            log.warning("Cannot register GameRule " + ruleClass.getCanonicalName() + ". " +
                    "A rule with the same name (" + name + ") already exists: " + ruleTypes.get(name).getCanonicalName()
                    + ". Annotate the rule with @RuleName and give it a unique name.");
            return;
        }
        ruleTypes.put(name, ruleClass);
        gameRuleSuppliers.put(ruleClass, supplier);
    }

    @SuppressWarnings("unchecked")
    public <TRule extends GameRule> TRule create(Class<TRule> ruleClass) {
        return (TRule) gameRuleSuppliers.get(ruleClass).get();
    }

    public Optional<Class<? extends GameRule>> getByName(String name) {
        return Optional.ofNullable(ruleTypes.get(name));
    }

    private String getRuleName(@NonNull Class<? extends GameRule> ruleClass) {
        if (ruleClass.isAnnotationPresent(RuleName.class))
            return ruleClass.getAnnotation(RuleName.class).value();
        else
            return GameRule.getName(ruleClass);
    }
}
