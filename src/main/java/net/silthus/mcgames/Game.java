package net.silthus.mcgames;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    public static final String DEFAULT_GAME_TITLE = "Unknown Game";

    @Getter
    private String title = DEFAULT_GAME_TITLE;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private GameState state = GameState.NOT_STARTED;
    private final Set<Player> players = new HashSet<>();
    private final Set<Player> spectators = new HashSet<>();

    public void setTitle(String title) {
        if (title == null || title.trim().equals(""))
            title = DEFAULT_GAME_TITLE;
        this.title = title;
    }

    public Collection<Player> getPlayers() {
        return Set.copyOf(players);
    }

    public Collection<Player> getSpectators() {
        return Set.copyOf(spectators);
    }

    public Collection<Player> getAllPlayers() {
        return Stream.concat(getPlayers().stream(), getSpectators().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isPlaying(Player player) {
        return getPlayers().contains(player);
    }

    public boolean isSpectating(Player player) {
        return spectators.contains(player);
    }

    public void start() {
        setState(GameState.STARTED);
    }

    public void stop() {
        setState(GameState.STOPPED);
    }

    public void join(@NonNull Player player) {
        players.add(player);
    }

    public void quit(@NonNull Player player) {
        players.remove(player);
        spectators.remove(player);
    }

    public void spectate(@NonNull Player player) {
        players.remove(player);
        spectators.add(player);
    }

    public void broadcast(String message) {
        getPlayers().forEach(player -> player.sendMessage(getPrefix() + message));
    }

    private String getPrefix() {
        return ChatColor.AQUA + "[" + ChatColor.GOLD + getTitle() + ChatColor.AQUA + "] " + ChatColor.RESET;
    }
}
