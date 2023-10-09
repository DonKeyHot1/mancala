package ru.mnagovitsin.mancala.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mnagovitsin.mancala.event.ParticipationCreatedEvent;
import ru.mnagovitsin.mancala.exception.GameNotFoundException;
import ru.mnagovitsin.mancala.mapper.GameMapper;
import ru.mnagovitsin.mancala.model.CreateNewGameRequest;
import ru.mnagovitsin.mancala.model.Game;
import ru.mnagovitsin.mancala.model.GameState;
import ru.mnagovitsin.mancala.model.MakeMoveRequest;
import ru.mnagovitsin.mancala.model.Participation;
import ru.mnagovitsin.mancala.repository.GameRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ru.mnagovitsin.mancala.model.Game.Status.WAITING_FOR_PLAYERS;

@Service
@RequiredArgsConstructor
public class GameService {

    private static final Set<Game.Status> AVAILABLE_STATUSES = Set.of(
        WAITING_FOR_PLAYERS
    );

    private static final int[] NEW_GAME_BOARD = new int[] {
        6, 6, 6, 6, 6, 6, 0,
        6, 6, 6, 6, 6, 6, 0
    };

    private final GameRepository gameRepository;
    private final MancalaMoveProcessor mancalaMoveProcessor;
    private final ParticipationService participationService;
    private final PlayerService playerService;
    private final SimpMessagingTemplate template;
    private final GameMapper gameMapper;

    @Transactional
    public Game create(CreateNewGameRequest createNewGameRequest) {
        return gameRepository.insertOnConflictDoReturn(
            createNewGameRequest.getRequestId(),
            createNewGameRequest.getTitle(),
            NEW_GAME_BOARD,
            true,
            WAITING_FOR_PLAYERS
        );
    }

    public GameState getGameState(UUID gameId, UUID playerId) {
        var game = gameRepository.getById(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId));

        var participation = participationService.getByGameAndPlayerId(gameId, playerId);
        var opponentParticipation = participationService.findOpponent(gameId, playerId);
        var player = playerService.getPlayer(participation.playerId());

        return gameMapper.toGameState(
            game,
            participation.isPlayer1(),
            player,
            opponentParticipation
                .map(Participation::playerId)
                .map(playerService::getPlayer)
                .orElse(null)
        );
    }

    @Transactional
    public GameState makeMove(UUID gameId, UUID playerId, MakeMoveRequest makeMoveRequest) {
        var game = gameRepository.getByIdForUpdate(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId));

        var gameStateAfterMove = mancalaMoveProcessor.processMove(game, makeMoveRequest.getPitIndex());
        gameRepository.update(
            gameStateAfterMove.id(),
            gameStateAfterMove.board(),
            gameStateAfterMove.isPlayer1Turn(),
            gameStateAfterMove.status()
        );

        var participation = participationService.getByGameAndPlayerId(gameId, playerId);
        var opponentParticipation = participationService.getOpponent(gameId, playerId);

        var opponentPlayerId = opponentParticipation.playerId();

        var player = playerService.getPlayer(playerId);
        var opponentPlayer = playerService.getPlayer(opponentPlayerId);

        GameState selfGameState = gameMapper.toGameState(
            gameStateAfterMove, participation.isPlayer1(), player, opponentPlayer);
        GameState opponentGameState = gameMapper.toGameState(
            gameStateAfterMove, opponentParticipation.isPlayer1(), opponentPlayer, player);

        template.convertAndSend("/topic/game-updates/" + playerId, selfGameState);
        template.convertAndSend("/topic/game-updates/" + opponentPlayerId, opponentGameState);

        return selfGameState;
    }

    public List<Game> getAvailableGames(Integer limit, Integer offset) {
        return gameRepository.findByStatusesOrderByTitle(AVAILABLE_STATUSES, limit, offset);
    }

    @EventListener(ParticipationCreatedEvent.class)
    public void onParticipationCreated(ParticipationCreatedEvent event) {
        var participation = (Participation) event.getSource();
        var gameId = participation.gameId();
        var playerId = participation.playerId();

        if (participationService.countByGameId(gameId) == 2
            && gameRepository.updateStatus(gameId, Game.Status.IN_PROGRESS) > 0) {

            var opponentParticipation = participationService.getOpponent(gameId, playerId);
            var opponentGameState = getGameState(gameId, opponentParticipation.playerId());

            template.convertAndSend("/topic/game-updates/" + opponentParticipation.playerId(), opponentGameState);
        }
    }
}
