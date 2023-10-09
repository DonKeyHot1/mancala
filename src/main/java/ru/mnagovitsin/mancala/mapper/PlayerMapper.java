package ru.mnagovitsin.mancala.mapper;

import org.springframework.stereotype.Component;
import ru.mnagovitsin.mancala.model.CreateNewPlayerResponse;
import ru.mnagovitsin.mancala.model.GetPlayerResponse;
import ru.mnagovitsin.mancala.model.Player;

@Component
public class PlayerMapper {

    public CreateNewPlayerResponse toCreateNewPlayerResponse(Player player) {
        return new CreateNewPlayerResponse(
            player.id()
        );
    }

    public GetPlayerResponse toGetPlayerResponse(Player player) {
        return new GetPlayerResponse(
            player.id(),
            player.nickname()
        );
    }
}
