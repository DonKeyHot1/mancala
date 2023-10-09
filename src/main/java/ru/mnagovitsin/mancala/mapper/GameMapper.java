package ru.mnagovitsin.mancala.mapper;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import ru.mnagovitsin.mancala.model.CreateNewGameResponse;
import ru.mnagovitsin.mancala.model.Game;
import ru.mnagovitsin.mancala.model.GameBoardState;
import ru.mnagovitsin.mancala.model.GameState;
import ru.mnagovitsin.mancala.model.GameStatePlayer;
import ru.mnagovitsin.mancala.model.GameStatePlayers;
import ru.mnagovitsin.mancala.model.GameStatus;
import ru.mnagovitsin.mancala.model.GetAvailableGamesResponseItem;
import ru.mnagovitsin.mancala.model.Player;
import ru.mnagovitsin.mancala.model.PlayerBoardState;
import ru.mnagovitsin.mancala.model.PlayerBoardStatePit;

import java.util.stream.IntStream;

@Component
public class GameMapper {

    public GameState toGameState(Game game,
                                 boolean currentPlayerIsPlayer1,
                                 Player selfPlayer,
                                 @Nullable Player opponentPlayer) {

        var wholeBoard = game.board();

        int[] opponentPitIndexes;
        int[] selfPitIndexes;
        int opponentMancalaIndex;
        int selfMancalaIndex;

        if (currentPlayerIsPlayer1) {
            opponentPitIndexes = new int[] { 12, 11, 10, 9, 8, 7 };
            selfPitIndexes = new int[] { 0, 1, 2, 3, 4, 5 };
            opponentMancalaIndex = 13;
            selfMancalaIndex = 6;
        } else {
            opponentPitIndexes = new int[] { 5, 4, 3, 2, 1, 0 };
            selfPitIndexes = new int[] { 7, 8, 9, 10, 11, 12 };
            opponentMancalaIndex = 6;
            selfMancalaIndex = 13;
        }

        var opponentBoardState = new PlayerBoardState(
            IntStream.of(opponentPitIndexes)
                .boxed()
                .map(i -> new PlayerBoardStatePit()
                    .index(i)
                    .stones(wholeBoard[i]))
                .toList(),
            wholeBoard[opponentMancalaIndex]
        );

        var selfBoardState = new PlayerBoardState(
            IntStream.of(selfPitIndexes)
                .boxed()
                .map(i -> new PlayerBoardStatePit()
                    .index(i)
                    .stones(wholeBoard[i]))
                .toList(),
            wholeBoard[selfMancalaIndex]
        );

        return new GameState(
            game.title(),
            new GameBoardState(
                selfBoardState,
                opponentBoardState
            ),
            mapStatus(game, currentPlayerIsPlayer1),
            new GameStatePlayers(
                new GameStatePlayer(selfPlayer.nickname()),
                new GameStatePlayer(opponentPlayer != null ? opponentPlayer.nickname() : null)
            )
        );
    }

    private GameStatus mapStatus(Game game, boolean currentPlayerIsPlayer1) {
        var status = game.status();
        switch (status) {
            case WAITING_FOR_PLAYERS:
                return GameStatus.WAITING_FOR_PLAYERS;
            case IN_PROGRESS:
                var isSelfTurn = (game.isPlayer1Turn() && currentPlayerIsPlayer1)
                                 || (!game.isPlayer1Turn() && !currentPlayerIsPlayer1);
                if (isSelfTurn) {
                    return GameStatus.SELF_TURN;
                } else {
                    return GameStatus.OPPONENT_TURN;
                }
            case PLAYER_1_WON:
                if (currentPlayerIsPlayer1) {
                    return GameStatus.SELF_WIN;
                } else {
                    return GameStatus.OPPONENT_WIN;
                }
            case PLAYER_2_WON:
                if (!currentPlayerIsPlayer1) {
                    return GameStatus.SELF_WIN;
                } else {
                    return GameStatus.OPPONENT_WIN;
                }
            case DRAW:
                return GameStatus.DRAW;
            default:
                throw new IllegalArgumentException("Unknown game status: " + status);
        }
    }

    public GetAvailableGamesResponseItem toGetAvailableGamesResponseItem(Game game) {
        return new GetAvailableGamesResponseItem(
            game.id(),
            game.title()
        );
    }

    public CreateNewGameResponse toCreateNewGameResponse(Game game) {
        return new CreateNewGameResponse(
            game.id()
        );
    }
}
