package ru.mnagovitsin.mancala.service;

import org.springframework.stereotype.Service;
import ru.mnagovitsin.mancala.exception.IllegalMoveException;
import ru.mnagovitsin.mancala.model.Game;

import java.util.Arrays;
import java.util.Set;

import static ru.mnagovitsin.mancala.model.Game.Status.*;
import static ru.mnagovitsin.mancala.model.Game.Status.PLAYER_1_WON;

@Service
public class MancalaMoveProcessor {

    private static final Set<Game.Status> IN_PROGRESS_STATUSES = Set.of(
        WAITING_FOR_PLAYERS,
        IN_PROGRESS
    );

    public Game processMove(Game game, int pitIndex) {

        checkMoveIsValid(game, pitIndex);

        var board = Arrays.copyOf(game.board(), game.board().length);
        var isPlayer1Turn = game.isPlayer1Turn();

        var stonesInHand = board[pitIndex];
        board[pitIndex] = 0;
        var pitToSow = pitIndex;

        while (stonesInHand > 0) {
            pitToSow = (pitToSow + 1) % 14;
            if (isPlayer1Turn && pitToSow == 13) {
                continue;
            }
            if (!isPlayer1Turn && pitToSow == 6) {
                continue;
            }
            board[pitToSow]++;
            stonesInHand--;
        }

        if (isPlayer1Turn && pitToSow < 6 && board[pitToSow] == 1) {
            var oppositePitIndex = 12 - pitToSow;
            board[6] += board[oppositePitIndex] + 1;
            board[oppositePitIndex] = 0;
            board[pitToSow] = 0;
        }

        if (!isPlayer1Turn && pitToSow > 6 && pitToSow != 13 && board[pitToSow] == 1) {
            var oppositePitIndex = 12 - pitToSow;
            board[13] += board[oppositePitIndex] + 1;
            board[oppositePitIndex] = 0;
            board[pitToSow] = 0;
        }

        Game.Status gameStatus;
        if (!isGameOver(board)) {
            gameStatus = IN_PROGRESS;
        } else {
            board[6] += Arrays.stream(Arrays.copyOfRange(board, 0, 6)).sum();
            board[13] += Arrays.stream(Arrays.copyOfRange(board, 7, 13)).sum();
            Arrays.fill(board, 0, 6, 0);
            Arrays.fill(board, 7, 13, 0);

            if (board[6] > board[13]) {
                gameStatus = PLAYER_1_WON;
            } else if (board[6] < board[13]) {
                gameStatus = PLAYER_2_WON;
            } else {
                gameStatus = DRAW;
            }
        }

        return new Game(game.id(), game.title(), board, isNextTurnPlayer1Turn(isPlayer1Turn, pitToSow), gameStatus);
    }

    private boolean isNextTurnPlayer1Turn(boolean isPlayer1Turn, int pitToSow) {
        if (isPlayer1Turn) {
            return pitToSow == 6;
        } else {
            return pitToSow != 13;
        }
    }

    private static void checkMoveIsValid(Game game, int pitIndex) {
        if (!IN_PROGRESS_STATUSES.contains(game.status())) {
            throw new IllegalMoveException("Game is over");
        }

        if (pitIndex < 0 || pitIndex > 12) {
            throw new IllegalMoveException("Pit index must be between 0 and 12");
        }

        if (game.board()[pitIndex] == 0) {
            throw new IllegalMoveException("Pit is empty");
        }

        if (game.isPlayer1Turn() && pitIndex > 5) {
            throw new IllegalMoveException("Pit index must be between 0 and 5 for player 1");
        }

        if (!game.isPlayer1Turn() && pitIndex < 7) {
            throw new IllegalMoveException("Pit index must be between 7 and 12 for player 2");
        }
    }

    private boolean isGameOver(int[] board) {
        var player1PitsEmpty = true;
        var player2PitsEmpty = true;
        for (int i = 0; i < 6; i++) {
            if (board[i] > 0) {
                player1PitsEmpty = false;
                break;
            }
        }
        for (int i = 7; i < 13; i++) {
            if (board[i] > 0) {
                player2PitsEmpty = false;
                break;
            }
        }
        return player1PitsEmpty || player2PitsEmpty;
    }
}
