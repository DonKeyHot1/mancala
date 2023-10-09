package ru.mnagovitsin.mancala.model;

import java.util.UUID;

public record Participation(
    UUID id,
    UUID playerId,
    UUID gameId,
    boolean isPlayer1
) {}
