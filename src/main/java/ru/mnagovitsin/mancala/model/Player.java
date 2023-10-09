package ru.mnagovitsin.mancala.model;

import java.util.UUID;

public record Player(
    UUID id,
    String nickname
) {}
