package net.silthus.mcgames;

import lombok.*;
import lombok.experimental.Accessors;
import net.silthus.mcgames.events.JoinGameEvent;
import net.silthus.mcgames.events.JoinedGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Game {

    public static final String DEFAULT_GAME_TITLE = "Unknown Game";

    private final GameMode gameMode;
    private final Set<GamePlayer> players = new HashSet<>();

    private String title = DEFAULT_GAME_TITLE;
    @Setter(AccessLevel.PRIVATE)
    private GameState state = GameState.NOT_STARTED;

    public Game(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().equals(""))
            title = DEFAULT_GAME_TITLE;
        this.title = title;
    }

    public Collection<Player> getPlayers() {
        return players.stream()
                .filter(GamePlayer::isPlaying)
                .map(GamePlayer::player)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<Player> getSpectators() {
        return players.stream()
                .filter(GamePlayer::isSpectating)
                .map(GamePlayer::player)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<Player> getAllPlayers() {
        return Stream.concat(getPlayers().stream(), getSpectators().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isPlaying(Player player) {
        return getGamePlayer(player)
                .map(GamePlayer::isPlaying)
                .orElse(false);
    }

    public boolean isSpectating(Player player) {
        return getGamePlayer(player)
                .map(GamePlayer::isSpectating)
                .orElse(false);
    }

    public int score(Player player) {
        return getGamePlayer(player)
                .map(GamePlayer::score)
                .orElse(0);
    }

    public Game score(Player player, int score) {
        getGamePlayer(player).ifPresent(gamePlayer ->
                gamePlayer.score(score)
        );
        return this;
    }

    public void start() {
        setState(GameState.STARTED);
    }

    public void stop() {
        setState(GameState.STOPPED);
    }

    public void join(@NonNull Player player) {
        if (fireJoinGameEvent(player).isCancelled()) return;

        if (canJoin()) {
            throw new GameException(player.getName() + " cannot join the game. The game is full.");
        }

        addOrUpdatePlayer(player, GamePlayer.Status.PLAYING);
        fireJoinedGameEvent(player);
    }

    public boolean canJoin() {
        return gameMode.hasMaxPlayerLimit()
                && getPlayers().size() >= gameMode.getMaxPlayers();
    }

    public void spectate(@NonNull Player player) {
        addOrUpdatePlayer(player, GamePlayer.Status.SPECTATING);
    }

    public void quit(@NonNull Player player) {
        players.remove(new GamePlayer(player));
    }

    public void broadcast(String message) {
        getPlayers().forEach(player -> player.sendMessage(prefix() + message));
    }

    private void addOrUpdatePlayer(Player player, GamePlayer.Status status) {
        if (!players.add(new GamePlayer(player).status(status))) {
            getGamePlayer(player).ifPresent(gamePlayer -> gamePlayer.status(status));
        }
    }

    private Optional<GamePlayer> getGamePlayer(Player player) {
        return players.stream()
                .filter(gamePlayer -> gamePlayer.player().equals(player))
                .findFirst();
    }

    private String prefix() {
        return ChatColor.AQUA + "[" + ChatColor.GOLD + getTitle() + ChatColor.AQUA + "] " + ChatColor.RESET;
    }

    private JoinGameEvent fireJoinGameEvent(Player player) {
        JoinGameEvent joinGameEvent = new JoinGameEvent(this, player);
        Bukkit.getPluginManager().callEvent(joinGameEvent);
        return joinGameEvent;
    }

    private void fireJoinedGameEvent(Player player) {
        Bukkit.getPluginManager().callEvent(new JoinedGameEvent(this, player));
    }

    @Data
    @Accessors(fluent = true)
    @EqualsAndHashCode(of = "player")
    private static class GamePlayer {



        private final Player player;
        private Status status = Status.PLAYING;
        private int score = 0;

        private GamePlayer(Player player) {
            this.player = player;
        }

        public boolean isSpectating() {
            return status == Status.SPECTATING;
        }

        public boolean isPlaying() {
            return status == Status.PLAYING;
        }


        enum Status {
            PLAYING,
            SPECTATING
        }
    }
}
