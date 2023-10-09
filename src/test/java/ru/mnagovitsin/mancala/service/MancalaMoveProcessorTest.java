package ru.mnagovitsin.mancala.service;

import org.junit.jupiter.api.Test;
import ru.mnagovitsin.mancala.exception.IllegalMoveException;
import ru.mnagovitsin.mancala.model.Game;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.mnagovitsin.mancala.model.Game.Status.*;

public class MancalaMoveProcessorTest {

    private final MancalaMoveProcessor mancalaMoveProcessor = new MancalaMoveProcessor();

    @Test
    public void whenPlayer1TakesTurnThenNextTurnIsPlayer2() {

        var givenState = newGameState(
            new int[] {
                6, 6, 6, 6, 6, 6, 0,
                6, 6, 6, 6, 6, 6, 0
            },
            true,
            IN_PROGRESS
        );

        var movePitIndex = 1;

        var expectedState = newGameState(
            new int[] {
                6, 0, 7, 7, 7, 7, 1,
                7, 6, 6, 6, 6, 6, 0
            },
            false,
            IN_PROGRESS
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenPlayer2TakesTurnThenNextTurnIsPlayer1() {

        var givenState = newGameState(
            new int[] {
                6, 6, 6, 6, 6, 6, 0,
                6, 6, 6, 6, 6, 6, 0
            },
            false,
            IN_PROGRESS
        );

        var movePitIndex = 8;

        var expectedState = newGameState(
            new int[] {
                7, 6, 6, 6, 6, 6, 0,
                6, 0, 7, 7, 7, 7, 1
            },
            true,
            IN_PROGRESS
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenPlayer1FinishesInHisMancalaThenHeTakesAnotherTurn() {

        var givenState = newGameState(
            new int[] {
                6, 6, 6, 6, 6, 6, 0,
                6, 6, 6, 6, 6, 6, 0
            },
            true,
            IN_PROGRESS
        );

        var movePitIndex = 0;

        var expectedState = newGameState(
            new int[] {
                0, 7, 7, 7, 7, 7, 1,
                6, 6, 6, 6, 6, 6, 0
            },
            true,
            IN_PROGRESS
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenPlayer2FinishesInHisMancalaThenHeTakesAnotherTurn() {
        var givenState = newGameState(
            new int[] {
                6, 6, 6, 6, 6, 6, 0,
                6, 6, 6, 6, 6, 6, 0
            },
            false,
            IN_PROGRESS
        );

        var movePitIndex = 7;

        var expectedState = newGameState(
            new int[] {
                6, 6, 6, 6, 6, 6, 0,
                0, 7, 7, 7, 7, 7, 1
            },
            false,
            IN_PROGRESS
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenPlayer1FinishesInEmptyPitThenHeTakesOppositeStonesAndHisStone() {
        var givenState = newGameState(
            new int[] {
                35, 0, 0, 0, 1, 0, 0,
                6, 6, 6, 6, 6, 6, 0
            },
            true,
            IN_PROGRESS
        );

        var movePitIndex = 4;

        var expectedState = newGameState(
            new int[] {
                35, 0, 0, 0, 0, 0, 7,
                0, 6, 6, 6, 6, 6, 0
            },
            false,
            IN_PROGRESS
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenPlayer2FinishesInEmptyPitThenHeTakesOppositeStonesAndHisStone() {
        var givenState = newGameState(
            new int[] {
                6, 6, 6, 6, 6, 6, 0,
                35, 0, 0, 0, 1, 0, 0
            },
            false,
            IN_PROGRESS
        );

        var movePitIndex = 11;

        var expectedState = newGameState(
            new int[] {
                0, 6, 6, 6, 6, 6, 0,
                35, 0, 0, 0, 0, 0, 7
            },
            true,
            IN_PROGRESS
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenPlayer1TakesLastOpponentStonesThenGameIsOver() {
        var givenState = newGameState(
            new int[] {
                5, 4, 3, 2, 1, 0, 50,
                7, 0, 0, 0, 0, 0, 0
            },
            true,
            IN_PROGRESS
        );

        var movePitIndex = 4;

        var expectedState = newGameState(
            new int[] {
                0, 0, 0, 0, 0, 0, 72,
                0, 0, 0, 0, 0, 0, 0
            },
            false,
            PLAYER_1_WON
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenPlayer2TakesLastOpponentStonesThenGameIsOver() {
        var givenState = newGameState(
            new int[] {
                7, 0, 0, 0, 0, 0, 0,
                5, 4, 3, 2, 1, 0, 50
            },
            false,
            IN_PROGRESS
        );

        var movePitIndex = 11;

        var expectedState = newGameState(
            new int[] {
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 72
            },
            true,
            PLAYER_2_WON
        );

        doTest(givenState, movePitIndex, expectedState);
    }

    @Test
    public void whenGameIsOverThenMoveIsNotPossible() {
        var givenState = newGameState(
            new int[] {
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 72
            },
            true,
            PLAYER_2_WON
        );

        doTestIllegalMove(givenState, 0, "Game is over");
    }

    @Test
    public void whenPitIndexIsIllegalThenMoveIsNotPossible() {
        var givenState = newGameState(
            new int[] {
                0, 0, 0, 0, 0, 0, 0,
                5, 4, 3, 2, 0, 0, 58
            },
            true,
            IN_PROGRESS
        );

        doTestIllegalMove(givenState, -1, "Pit index must be between 0 and 12");
    }

    @Test
    public void whenPitIsEmptyThenMoveIsNotPossible() {
        var givenState = newGameState(
            new int[] {
                0, 1, 0, 0, 0, 0, 0,
                5, 4, 3, 2, 0, 0, 57
            },
            true,
            IN_PROGRESS
        );

        doTestIllegalMove(givenState, 0, "Pit is empty");
    }

    @Test
    public void whenPitIndexIsIllegalForPlayer1ThenMoveIsNotPossible() {
        var givenState = newGameState(
            new int[] {
                0, 1, 0, 0, 0, 0, 0,
                5, 4, 3, 2, 0, 0, 57
            },
            true,
            IN_PROGRESS
        );

        doTestIllegalMove(givenState, 7, "Pit index must be between 0 and 5 for player 1");
    }

    @Test
    public void whenPitIndexIsIllegalForPlayer2ThenMoveIsNotPossible() {
        var givenState = newGameState(
            new int[] {
                0, 1, 0, 0, 0, 0, 0,
                5, 4, 3, 2, 0, 0, 57
            },
            false,
            IN_PROGRESS
        );

        doTestIllegalMove(givenState, 1, "Pit index must be between 7 and 12 for player 2");
    }

    private Game newGameState(int[] board, boolean isPlayer1Turn, Game.Status status) {
        return new Game(
            UUID.randomUUID(),
            "test game",
            board,
            isPlayer1Turn,
            status
        );
    }

    private void doTest(Game givenState, int movePitIndex, Game expectedState) {
        var resultState = mancalaMoveProcessor.processMove(givenState, movePitIndex);
        assertArrayEquals(expectedState.board(), resultState.board());
        assertEquals(expectedState.isPlayer1Turn(), resultState.isPlayer1Turn());
        assertEquals(expectedState.status(), resultState.status());
    }

    private void doTestIllegalMove(Game givenState, int movePitIndex, String expectedMessage) {
        var exception = assertThrows(
            IllegalMoveException.class,
            () -> mancalaMoveProcessor.processMove(givenState, movePitIndex)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }
}
