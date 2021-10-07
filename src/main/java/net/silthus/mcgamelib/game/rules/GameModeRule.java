package net.silthus.mcgamelib.game.rules;

import lombok.Getter;
import lombok.Setter;
import net.silthus.mcgamelib.event.GameEvent;
import net.silthus.mcgamelib.events.JoinedGameEvent;
import net.silthus.mcgamelib.events.QuitGameEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
public class GameModeRule implements GameRule {

    private final Map<UUID, GameMode> oldGameModes = new HashMap<>();
    private GameMode gameMode = GameMode.SURVIVAL;

    @GameEvent
    public void onPlayerJoin(JoinedGameEvent event) {
        Player player = event.getPlayer();
        updateGameMode(player, gameMode);
    }

    @GameEvent
    public void onPlayerQuit(QuitGameEvent event) {
        Player player = event.getPlayer();
        updateGameMode(player, oldGameModes.get(player.getUniqueId()));
    }

    private void updateGameMode(Player player, GameMode gameMode) {
        if (gameMode != null) {
            oldGameModes.put(player.getUniqueId(), player.getGameMode());
            player.setGameMode(gameMode);
        }
    }
}
