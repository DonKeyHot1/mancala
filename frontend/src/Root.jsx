import {Button, Stack, Typography} from "@mui/material";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {getCurrentPlayerId} from "./clientStorage.js";
import {playerApiClient} from "./apiClients.js";

export default function Root() {
    const navigate = useNavigate();
    const [currentPlayerNickname, setCurrentPlayerNickname] = useState(null);

    useEffect(() => {
        if (!getCurrentPlayerId()) {
            navigate('/create-player')
        } else {
            playerApiClient.getPlayer(getCurrentPlayerId(), (error, response) => {
                if (error) {
                    console.error(error);
                    return;
                }
                setCurrentPlayerNickname(response.nickname);
            })
        }
    }, []);

    return (
        <Stack direction={'column'} spacing={2}>
            <Typography variant={'h4'}>Hello, {currentPlayerNickname}</Typography>
            <Stack direction={`row`} spacing={2}>
                <Button variant={`contained`} onClick={() => navigate(`/create-game`)}>Create new game</Button>
                <Button variant={`contained`} onClick={() => navigate(`/join-game`)}>Join game</Button>
            </Stack>
        </Stack>
    )
}
