package ru.mnagovitsin.mancala.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mnagovitsin.mancala.api.PlayerApi;
import ru.mnagovitsin.mancala.mapper.PlayerMapper;
import ru.mnagovitsin.mancala.model.CreateNewPlayerRequest;
import ru.mnagovitsin.mancala.model.CreateNewPlayerResponse;
import ru.mnagovitsin.mancala.model.GetPlayerResponse;
import ru.mnagovitsin.mancala.service.PlayerService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PlayerController implements PlayerApi {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    @Override
    public ResponseEntity<CreateNewPlayerResponse> createNewPlayer(CreateNewPlayerRequest createNewPlayerRequest) {
        return ResponseEntity.ok(
            playerMapper.toCreateNewPlayerResponse(
                playerService.create(createNewPlayerRequest)
            )
        );
    }

    @Override
    public ResponseEntity<GetPlayerResponse> getPlayer(UUID playerId) {
        return ResponseEntity.ok(
            playerMapper.toGetPlayerResponse(
                playerService.getPlayer(playerId)
            )
        );
    }
}
