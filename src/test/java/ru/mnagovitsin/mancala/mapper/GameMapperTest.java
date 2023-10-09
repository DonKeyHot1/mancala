package ru.mnagovitsin.mancala.mapper;

import org.junit.jupiter.api.Test;
import ru.mnagovitsin.mancala.model.Game;
import ru.mnagovitsin.mancala.model.GameBoardState;
import ru.mnagovitsin.mancala.model.GameState;
import ru.mnagovitsin.mancala.model.GameStatePlayer;
import ru.mnagovitsin.mancala.model.GameStatePlayers;
import ru.mnagovitsin.mancala.model.GameStatus;
import ru.mnagovitsin.mancala.model.Player;
import ru.mnagovitsin.mancala.model.PlayerBoardState;
import ru.mnagovitsin.mancala.model.PlayerBoardStatePit;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.mnagovitsin.mancala.model.Game.Status.IN_PROGRESS;

// TODO: finish mapping tests
class GameMapperTest {

    private static final String TEST_TITLE = "test title";

    private final GameMapper mapper = new GameMapper();

    @Test
    void whenPlayer2TurnThenSelfGameStateIsCorrect() {
        var givenGame = givenGame(
            new int[] {
                5, 0, 1, 12, 1, 0, 11,
                1, 5, 11, 11, 11, 4, 3
            },
            false,
            IN_PROGRESS
        );

        var givenSelfPlayer = new Player(UUID.randomUUID(), "self player");
        var givenOpponentPlayer = new Player(UUID.randomUUID(), "opponent player");

        var expectedSelfGameState = new GameState(
            TEST_TITLE,
            new GameBoardState(
                new PlayerBoardState(
                    List.of(
                        new PlayerBoardStatePit().index(7).stones(1),
                        new PlayerBoardStatePit().index(8).stones(5),
                        new PlayerBoardStatePit().index(9).stones(11),
                        new PlayerBoardStatePit().index(10).stones(11),
                        new PlayerBoardStatePit().index(11).stones(11),
                        new PlayerBoardStatePit().index(12).stones(4)
                    ),
                    3
                ),
                new PlayerBoardState(
                    List.of(
                        new PlayerBoardStatePit().index(5).stones(0),
                        new PlayerBoardStatePit().index(4).stones(1),
                        new PlayerBoardStatePit().index(3).stones(12),
                        new PlayerBoardStatePit().index(2).stones(1),
                        new PlayerBoardStatePit().index(1).stones(0),
                        new PlayerBoardStatePit().index(0).stones(5)
                    ),
                    11
                )
            ),
            GameStatus.SELF_TURN,
            new GameStatePlayers(
                new GameStatePlayer(givenSelfPlayer.nickname()),
                new GameStatePlayer(givenOpponentPlayer.nickname())
            )
        );

        var selfGameState = mapper.toGameState(givenGame, false, givenSelfPlayer, givenOpponentPlayer);

        assertEquals(expectedSelfGameState, selfGameState);
    }

    private Game givenGame(int[] board, boolean isPlayer1Turn, Game.Status status) {
        return new Game(
            UUID.randomUUID(),
            TEST_TITLE,
            board,
            isPlayer1Turn,
            status
        );
    }
}
