package ru.mnagovitsin.mancala.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mnagovitsin.mancala.api.GameApi;
import ru.mnagovitsin.mancala.mapper.GameMapper;
import ru.mnagovitsin.mancala.model.CreateNewGameRequest;
import ru.mnagovitsin.mancala.model.CreateNewGameResponse;
import ru.mnagovitsin.mancala.model.GameState;
import ru.mnagovitsin.mancala.model.GetAvailableGamesResponse;
import ru.mnagovitsin.mancala.model.MakeMoveRequest;
import ru.mnagovitsin.mancala.service.GameService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameController implements GameApi {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @Override
    public ResponseEntity<CreateNewGameResponse> createNewGame(UUID playerId, CreateNewGameRequest createNewGameRequest) {
        return ResponseEntity.ok(
            gameMapper.toCreateNewGameResponse(
                gameService.create(createNewGameRequest)
            )
        );
    }

    @Override
    public ResponseEntity<GetAvailableGamesResponse> getAvailableGames(UUID xPlayerId, Integer limit, Integer offset) {
        return ResponseEntity.ok(
            new GetAvailableGamesResponse(
                gameService.getAvailableGames(limit, offset)
                    .stream()
                    .map(gameMapper::toGetAvailableGamesResponseItem)
                    .toList()
            )
        );
    }

    @Override
    public ResponseEntity<GameState> getGameState(UUID gameId, UUID playerId) {
        return ResponseEntity.ok(gameService.getGameState(gameId, playerId));
    }

    @Override
    public ResponseEntity<GameState> makeMove(UUID gameId, UUID playerId, MakeMoveRequest makeMoveRequest) {
        return ResponseEntity.ok(gameService.makeMove(gameId, playerId, makeMoveRequest));
    }
}
