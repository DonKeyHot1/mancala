import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {playerApiClient} from "./apiClients.js";
import {CreateNewPlayerRequest} from "./generated/src/index.js";
import {Button, Stack, TextField, Typography} from "@mui/material";
import {getCurrentPlayerId, setCurrentPlayerId} from "./clientStorage.js";
import {v4 as uuid} from "uuid";


export default function CreatePlayer() {
    const navigate= useNavigate();
    const [nickname, setNickname] = useState("");
    const [requestId, setRequestId] = useState(uuid());

    useEffect(() => {
        if (getCurrentPlayerId()) {
            window.location.reload()
        }
    }, []);

    const createPlayer = () => {
        playerApiClient.createNewPlayer(new CreateNewPlayerRequest(requestId, nickname), (error, response) => {
            if (error) {
                console.error(error);
                return;
            }
            const playerId = response.id;
            setCurrentPlayerId(playerId);
            navigate('/')
        })
    };

    return (
        <Stack
            direction={`column`}
            spacing={2}
        >
            <Typography variant={`h4`}>Create new player</Typography>
            <TextField
                label={`Nickname`}
                onChange={(event) => setNickname(event.target.value)}
            />
            <Button variant={`contained`} onClick={createPlayer} >Create</Button>
        </Stack>
    )
}
