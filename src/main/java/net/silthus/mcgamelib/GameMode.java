package net.silthus.mcgamelib;

import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"identifier"})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMode {

    public static final String DEFAULT_NAME = "Unnamed Game Mode";

    @Builder.Default
    String identifier = UUID.randomUUID().toString();
    @Builder.Default
    String name = DEFAULT_NAME;
    @Builder.Default
    int minPlayers = 0;
    @Builder.Default
    int maxPlayers = -1;

    public boolean hasMaxPlayerLimit() {
        return maxPlayers > 0;
    }

    public Game newGame() {
        return new Game(this);
    }

    public static class GameModeBuilder {

        public GameModeBuilder name(String name) {
            if (name == null || name.trim().equals(""))
                name = DEFAULT_NAME;

            this.name$value = name;
            this.name$set = true;
            return this;
        }

        public GameModeBuilder minPlayers(int minPlayers) {
            if (minPlayers < 0)
                minPlayers = 0;
            if (minPlayers > maxPlayers$value)
                maxPlayers(minPlayers);

            this.minPlayers$value = minPlayers;
            this.minPlayers$set = true;
            return this;
        }

        public GameModeBuilder maxPlayers(int maxPlayers) {
            if (maxPlayers > 0 && maxPlayers < minPlayers$value)
                minPlayers(maxPlayers);

            this.maxPlayers$value = maxPlayers;
            this.maxPlayers$set = true;
            return this;
        }
    }
}
