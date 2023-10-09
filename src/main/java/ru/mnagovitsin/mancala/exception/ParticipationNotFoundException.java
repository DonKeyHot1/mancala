package ru.mnagovitsin.mancala.exception;

import java.util.UUID;

public class ParticipationNotFoundException extends RuntimeException {
    public ParticipationNotFoundException(UUID gameId, UUID playerId, boolean isOpponent) {
        super(isOpponent ?
            "Opponent of participation with gameId " + gameId + " and playerId " + playerId + " not found" :
            "Participation with gameId " + gameId + " and playerId " + playerId + " not found");
    }
}
