package ru.mnagovitsin.mancala.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.mnagovitsin.mancala.model.Player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends Repository<Player, UUID> {

    @Query("""
        WITH new_player AS (
            INSERT INTO player (id, nickname)
            VALUES (:id, :nickname)
            ON CONFLICT (id) DO NOTHING
            RETURNING *
        )
        SELECT *
        FROM new_player
        UNION
        SELECT *
        FROM player
        WHERE id = :id
        """)
    Player insertOnConflictDoReturn(UUID id, String nickname);

    @Query("""
        SELECT *
        FROM player
        WHERE id = :playerId
        """)
    Optional<Player> getById(UUID playerId);

    @Query("""
        SELECT EXISTS (
            SELECT *
            FROM player
            WHERE id = :id
        )
        """)
    boolean existsById(UUID id);

    @Modifying
    @Query("""
        DELETE
        FROM player
        WHERE id = :id
        """)
    int deleteById(UUID id);
}
