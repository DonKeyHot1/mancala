package ru.mnagovitsin.mancala.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID gameId) {
        super("Game with id '" + gameId + "' not found");
    }
}
