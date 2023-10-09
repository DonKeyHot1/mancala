package ru.mnagovitsin.mancala.model;

import java.util.UUID;

public record Game(
    UUID id,
    String title,
    int[] board,
    boolean isPlayer1Turn,
    Status status
) {
    public enum Status {
        WAITING_FOR_PLAYERS,
        IN_PROGRESS,
        PLAYER_1_WON,
        PLAYER_2_WON,
        DRAW
    }
}
