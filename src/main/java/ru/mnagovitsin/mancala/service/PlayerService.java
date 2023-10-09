package ru.mnagovitsin.mancala.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mnagovitsin.mancala.exception.PlayerNotFoundException;
import ru.mnagovitsin.mancala.model.CreateNewPlayerRequest;
import ru.mnagovitsin.mancala.model.Player;
import ru.mnagovitsin.mancala.repository.PlayerRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Player create(CreateNewPlayerRequest createNewPlayerRequest) {
        return playerRepository.insertOnConflictDoReturn(
            createNewPlayerRequest.getRequestId(),
            createNewPlayerRequest.getNickname()
        );
    }

    public Player getPlayer(UUID playerId) {
        return playerRepository.getById(playerId)
            .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    public boolean existsById(UUID playerId) {
        return playerRepository.existsById(playerId);
    }
}
