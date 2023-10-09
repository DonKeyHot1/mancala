package ru.mnagovitsin.mancala.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mnagovitsin.mancala.api.ParticipationApi;
import ru.mnagovitsin.mancala.mapper.ParticipationMapper;
import ru.mnagovitsin.mancala.model.CreateNewParticipationRequest;
import ru.mnagovitsin.mancala.model.CreateNewParticipationResponse;
import ru.mnagovitsin.mancala.service.ParticipationService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ParticipationController implements ParticipationApi {

    private final ParticipationService participationService;
    private final ParticipationMapper participationMapper;

    @Override
    public ResponseEntity<CreateNewParticipationResponse> createNewParticipation(UUID playerId,
                                                                                 CreateNewParticipationRequest createNewParticipationRequest) {
        return ResponseEntity.ok(
            participationMapper.toCreateNewParticipationResponse(
                participationService.create(playerId, createNewParticipationRequest)
            )
        );
    }
}
