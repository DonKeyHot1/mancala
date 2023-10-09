package ru.mnagovitsin.mancala.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.mnagovitsin.mancala.model.Participation;

import java.util.Optional;
import java.util.UUID;

public interface ParticipationRepository extends Repository<Participation, UUID> {

    @Query("""
        WITH new_participation AS (
            INSERT INTO participation (id, game_id, player_id, is_player1)
            VALUES (:id, :gameId, :playerId, :isPlayer1)
            ON CONFLICT (id) DO NOTHING
            RETURNING *
        )
        SELECT * FROM new_participation
        UNION
        SELECT * FROM participation
        WHERE id = :id
        """)
    Participation insertOnConflictDoReturn(UUID id, UUID gameId, UUID playerId, boolean isPlayer1);

    @Query("""
        SELECT *
        FROM participation
        WHERE game_id = :gameId
          AND player_id = :playerId
        """)
    Optional<Participation> getByGameIdAndPlayerId(UUID gameId, UUID playerId);

    @Query("""
        SELECT count(*)
        FROM participation
        WHERE game_id = :gameId
        """)
    int countByGameId(UUID gameId);

    @Query("""
        SELECT *
        FROM participation
        WHERE game_id = :gameId
          AND player_id != :playerId
        """)
    Optional<Participation> getOpponentByGameIdAndPlayerId(UUID gameId, UUID playerId);
}
