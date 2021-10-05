package net.silthus.mcgames;

import lombok.*;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
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
    private final Set<GamePlayer> players = new HashSet<>();

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

    public int getScore(Player player) {
        return getGamePlayer(player)
                .map(GamePlayer::score)
                .orElse(0);
    }

    public void setScore(Player player, int score) {
        getGamePlayer(player).ifPresent(gamePlayer ->
                gamePlayer.score(score)
        );
    }

    public void start() {
        setState(GameState.STARTED);
    }

    public void stop() {
        setState(GameState.STOPPED);
    }

    public void join(@NonNull Player player) {
        addOrUpdatePlayer(player, GamePlayer.Status.PLAYING);
    }

    public void spectate(@NonNull Player player) {
        addOrUpdatePlayer(player, GamePlayer.Status.SPECTATING);
    }

    public void quit(@NonNull Player player) {
        players.remove(new GamePlayer(player));
    }

    public void broadcast(String message) {
        getPlayers().forEach(player -> player.sendMessage(getPrefix() + message));
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

    private String getPrefix() {
        return ChatColor.AQUA + "[" + ChatColor.GOLD + getTitle() + ChatColor.AQUA + "] " + ChatColor.RESET;
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
