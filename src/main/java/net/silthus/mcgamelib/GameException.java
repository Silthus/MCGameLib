package net.silthus.mcgamelib;

public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Exception exception) {
        super(message, exception);
    }
}
