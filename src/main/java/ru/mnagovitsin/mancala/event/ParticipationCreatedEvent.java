package ru.mnagovitsin.mancala.event;

import org.springframework.context.ApplicationEvent;
import ru.mnagovitsin.mancala.model.Participation;

public class ParticipationCreatedEvent extends ApplicationEvent {
    public ParticipationCreatedEvent(Participation source) {
        super(source);
    }
}
