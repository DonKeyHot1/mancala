import {Button, Stack, TextField, Typography} from "@mui/material";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {getCurrentPlayerId} from "./clientStorage.js";
import {gameApiClient, participationApiClient} from "./apiClients.js";
import {CreateNewParticipationRequest} from "./generated/src/index.js";
import {v4 as uuid} from "uuid";


export default function JoinGame() {
    const navigate= useNavigate();
    const [requestId, setRequestId] = useState(uuid());
    const [availableGames, setAvailableGames] = useState([]);

    useEffect(() => {
        if (!getCurrentPlayerId()) {
            navigate('/create-player')
        } else {
            gameApiClient.getAvailableGames(getCurrentPlayerId(), {
                limit: 10,
                offset: 0
            }, (error, response) => {
                if (error) {
                    console.error(error);
                    return;
                }
                setAvailableGames(response.items);
            });
        }
    }, []);

    const joinGame = (game) => () => {
        const request = new CreateNewParticipationRequest(
            requestId,
            false,
            game.id
        );
        participationApiClient.createNewParticipation(getCurrentPlayerId(), request, (error, response) => {
            if (error) {
                console.error(error);
                return;
            }
            navigate(`/game/${game.id}`);
        })
    };

    const renderGame = (game) => (
        <Stack direction={'row'} spacing={2} key={game.id}>
            <Typography variant={'h6'}>{game.title}</Typography>
            <Button variant={'contained'} onClick={joinGame(game)}>Join</Button>
        </Stack>
    )

    return (
        <Stack
            direction={`column`}
            spacing={2}
        >
            {availableGames.map(game => renderGame(game))}
        </Stack>
    )
}
