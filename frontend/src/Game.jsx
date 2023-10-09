import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {gameApiClient} from "./apiClients.js";
import {Button, Grid, Stack, Typography} from "@mui/material";
import {MakeMoveRequest} from "./generated/src/index.js";
import {useSubscription} from "react-stomp-hooks";
import {getCurrentPlayerId} from "./clientStorage.js";

const player1Indices = [];
for (let i = 0; i < 6; i++) {
    player1Indices.push(i);
}
const player2Indices = [];
for (let i = 12; i > 6; i--) {
    player2Indices.push(i);
}

export default function Game() {
    const params = useParams();
    const navigate = useNavigate();
    const gameId = params.gameId;
    const playerId = getCurrentPlayerId();
    const [game, setGame] = useState(null);

    useEffect(() => {
        if (!getCurrentPlayerId()) {
            navigate('/create-player')
        } else {
            gameApiClient.getGameState(gameId, playerId, (error, response) => {
                if (error) {
                    console.error(error);
                    return;
                }
                setGame(response);
            })
        }

    }, []);

    const topic = `/topic/game-updates/${playerId}`;
    useSubscription(topic, message => {
        setGame(JSON.parse(message.body))
    })

    const renderPit = (board, pit, isOpponent) => {

        const disabled = isOpponent || game.status === 'OPPONENT_TURN' || pit.stones < 1;

        const onClick = () => {
            gameApiClient.makeMove(gameId, playerId, new MakeMoveRequest(pit.index), (error, response) => {
                if (error) {
                    console.error(error);
                }
            });
        };
        return (
            <Grid xs={2} item key={pit.index}>
                <Button
                    variant={`contained`}
                    disabled={disabled}
                    onClick={onClick}
                >
                    {pit.stones}
                </Button>
            </Grid>
        )
    }

    const renderGame = (game) => {
        if (game.status === 'WAITING_FOR_PLAYERS') {
            return (
                <Stack direction={'column'} spacing={2}>
                    <Typography variant={'h4'}>Waiting for second player...</Typography>
                    <Button variant={'contained'} onClick={() => navigate('/')}>Quit</Button>
                </Stack>
            )
        }
        if (game.status === 'OPPONENT_WIN' || game.status === 'SELF_WIN') {
            const winnerNickname = game.status === 'OPPONENT_WIN'
                ? game.players.opponent.nickname
                : game.players.self.nickname;
            return (
                <Stack direction={'column'} spacing={2}>
                    <Typography variant={'h4'}>Game over.</Typography>
                    <Typography variant={'h4'}>{winnerNickname} won! Final score:</Typography>
                    <Typography variant={'h4'}>{game.players.opponent.nickname}: {game.board.opponent.score}</Typography>
                    <Typography variant={'h4'}>{game.players.self.nickname}: {game.board.self.score}</Typography>
                    <Button variant={'contained'} onClick={() => navigate('/')}>Quit</Button>
                </Stack>
            )
        }
        if (game.status === 'DRAW') {
            return (
                <Stack direction={'column'} spacing={2}>
                    <Typography variant={'h4'}>Game over. Draw!</Typography>
                    <Button variant={'contained'} onClick={() => navigate('/')}>Quit</Button>
                </Stack>
            )
        }

        const board = game.board;

        return (
            <Stack direction={'column'} spacing={10}>
                <Typography variant={'h4'}>{game.title}</Typography>
                <Grid container spacing={2} justifyContent="center" alignItems="center">
                    <Grid item xs={12} fontSize={36}>
                        <Typography variant={'h4'}>{game.players.opponent.nickname}</Typography>
                    </Grid>
                    <Grid item xs={1} fontSize={48}>{board.opponent.score}</Grid>
                    <Grid container item spacing={2} xs={10}>
                        {board.opponent.pits.map((i) =>
                            renderPit(board, i, true))}
                        {board.self.pits.map((i) =>
                            renderPit(board, i, false))}
                    </Grid>
                    <Grid item xs={1} fontSize={48}>{board.self.score}</Grid>
                    <Grid item xs={12} fontSize={36}>
                        <Typography variant={'h4'}>{game.players.self.nickname}</Typography>
                    </Grid>
                </Grid>
            </Stack>
        )
    }

    return game?.board ? renderGame(game) : (<>Loading...</>)
}
