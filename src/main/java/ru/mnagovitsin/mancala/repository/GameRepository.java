package ru.mnagovitsin.mancala.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.mnagovitsin.mancala.model.Game;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends Repository<Game, UUID> {

    @Query("""
        WITH new_game AS (
            INSERT INTO game (id, title, board, is_player1_turn, status)
            VALUES (:id, :title, :board, :isPlayer1Turn, :status)
            ON CONFLICT (id) DO NOTHING
            RETURNING *
        )
        SELECT *
        FROM new_game
        UNION
        SELECT *
        FROM game
        WHERE id = :id
        """)
    Game insertOnConflictDoReturn(UUID id,
                                  String title,
                                  int[] board,
                                  boolean isPlayer1Turn,
                                  Game.Status status);

    @Query("""
        UPDATE game
        SET board = :board,
            is_player1_turn = :isPlayer1Turn,
            status = :status
        WHERE id = :id
        RETURNING *
        """)
    Game update(UUID id,
                int[] board,
                boolean isPlayer1Turn,
                Game.Status status);

    @Query("""
        SELECT *
        FROM game
        WHERE id = :gameId
        FOR UPDATE
        """)
    Optional<Game> getByIdForUpdate(UUID gameId);

    @Query("""
        SELECT *
        FROM game
        WHERE id = :gameId
        """)
    Optional<Game> getById(UUID gameId);

    @Modifying
    @Query("""
        UPDATE game
        SET status = :status
        WHERE id = :id
        """)
    int updateStatus(UUID id, Game.Status status);

    @Query("""
        SELECT *
        FROM game
        WHERE status in (:statuses)
        ORDER BY title
        LIMIT :limit
        OFFSET :offset
        """)
    List<Game> findByStatusesOrderByTitle(Collection<Game.Status> statuses, Integer limit, Integer offset);
}
