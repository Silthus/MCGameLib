package net.silthus.mcgamelib.game.rules;

import lombok.Getter;
import lombok.Setter;
import net.silthus.mcgamelib.event.GameEvent;
import net.silthus.mcgamelib.events.PlayerJoinedGameEvent;
import net.silthus.mcgamelib.events.PlayerQuitGameEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class MaxHealthRule implements GameRule {

    private static final double DEFAULT_MAX_HEALTH = 20d;

    private final Map<UUID, Double> maxHealths = new HashMap<>();
    private double maxHealth = DEFAULT_MAX_HEALTH;

    @GameEvent
    public void onJoin(PlayerJoinedGameEvent event) {
        setMaxHealth(event.getPlayer(), maxHealth);
    }

    @GameEvent
    public void onQuit(PlayerQuitGameEvent event) {
        resetMaxHealth(event.getPlayer());
    }

    private void resetMaxHealth(Player player) {
        setMaxHealth(player, maxHealths.getOrDefault(player.getUniqueId(), DEFAULT_MAX_HEALTH));
    }

    private void setMaxHealth(Player player, double maxHealth) {
        double healthPercent = getHealthPercent(player);
        updateMaxHealth(player, maxHealth);
        player.setHealth(maxHealth * healthPercent);
    }

    private void updateMaxHealth(Player player, double maxHealth) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            maxHealths.put(player.getUniqueId(), attribute.getBaseValue());
            attribute.setBaseValue(maxHealth);
        }
    }

    private double getHealthPercent(Player player) {
        return player.getHealth() / getMaxHealth(player);
    }

    private double getMaxHealth(Player player) {
        return Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }
}
