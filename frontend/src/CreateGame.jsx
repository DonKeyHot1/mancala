import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {gameApiClient, participationApiClient} from "./apiClients.js";
import {CreateNewGameRequest, CreateNewParticipationRequest} from "./generated/src/index.js";
import {Button, Stack, TextField} from "@mui/material";
import {getCurrentPlayerId} from "./clientStorage.js";
import { v4 as uuid } from 'uuid';


export default function CreateGame() {
    const navigate = useNavigate();
    const [gameTitle, setGameTitle] = useState("");
    const [gameRequestId, setGameRequestId] = useState(uuid());
    const [participationRequestId, setParticipationRequestId] = useState(uuid());

    useEffect(() => {
        if (!getCurrentPlayerId()) {
            navigate('/create-player')
        }
    }, []);

    const createGame = () => {
        gameApiClient.createNewGame(
            getCurrentPlayerId(),
            new CreateNewGameRequest(gameRequestId, gameTitle),
            (error, response) => {
                if (error) {
                    console.error(error);
                    return;
                }

                const gameId = response.id;

                participationApiClient.createNewParticipation(
                    getCurrentPlayerId(),
                    new CreateNewParticipationRequest(participationRequestId, true, gameId),
                    (error, response) => {
                        if (error) {
                            console.error(error);
                            return;
                        }
                        navigate(`/game/${gameId}`);
                    })
        })
    };

    return (
        <Stack
            direction={`column`}
            spacing={2}
        >
            <TextField
                label={`Game title`}
                required
                onChange={(event) => setGameTitle(event.target.value)}
            />
            <Button variant={`contained`} onClick={createGame} >Create</Button>
        </Stack>
    )
}
