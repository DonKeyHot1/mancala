package ru.mnagovitsin.mancala.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlayerIdHolder {
    private static final ThreadLocal<UUID> PLAYER_ID = new ThreadLocal<>();

    public UUID get() {
        return PLAYER_ID.get();
    }

    public void set(UUID playerId) {
        PLAYER_ID.set(playerId);
    }
}
