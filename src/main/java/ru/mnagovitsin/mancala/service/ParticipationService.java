package ru.mnagovitsin.mancala.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.mnagovitsin.mancala.event.ParticipationCreatedEvent;
import ru.mnagovitsin.mancala.exception.ParticipationNotFoundException;
import ru.mnagovitsin.mancala.model.CreateNewParticipationRequest;
import ru.mnagovitsin.mancala.model.Participation;
import ru.mnagovitsin.mancala.repository.ParticipationRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Participation create(UUID playerId, CreateNewParticipationRequest createNewParticipationRequest) {
        var createdParticipation = create(
            createNewParticipationRequest.getRequestId(),
            createNewParticipationRequest.getGameId(),
            playerId,
            createNewParticipationRequest.getIsPlayer1()
        );

        eventPublisher.publishEvent(new ParticipationCreatedEvent(createdParticipation));

        return createdParticipation;
    }

    public Participation create(UUID id, UUID gameId, UUID playerId, boolean isPlayer1) {
        return participationRepository.insertOnConflictDoReturn(
            id, gameId, playerId, isPlayer1
        );
    }

    public Participation getByGameAndPlayerId(UUID gameId, UUID playerId) {
        return participationRepository.getByGameIdAndPlayerId(gameId, playerId)
            .orElseThrow(() -> new ParticipationNotFoundException(gameId, playerId, false));
    }

    public int countByGameId(UUID gameId) {
        return participationRepository.countByGameId(gameId);
    }

    public Participation getOpponent(UUID gameId, UUID playerId) {
        return participationRepository.getOpponentByGameIdAndPlayerId(gameId, playerId)
            .orElseThrow(() -> new ParticipationNotFoundException(gameId, playerId, true));
    }

    public Optional<Participation> findOpponent(UUID gameId, UUID playerId) {
        return participationRepository.getOpponentByGameIdAndPlayerId(gameId, playerId);
    }
}
