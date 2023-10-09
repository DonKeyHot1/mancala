package ru.mnagovitsin.mancala.mapper;

import org.springframework.stereotype.Component;
import ru.mnagovitsin.mancala.model.CreateNewParticipationResponse;
import ru.mnagovitsin.mancala.model.Participation;

@Component
public class ParticipationMapper {

    public CreateNewParticipationResponse toCreateNewParticipationResponse(Participation participation) {
        return new CreateNewParticipationResponse(
            participation.id()
        );
    }

}
