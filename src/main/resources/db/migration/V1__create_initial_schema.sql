CREATE TABLE player (
    id UUID PRIMARY KEY,
    nickname VARCHAR(255) NOT NULL
);

CREATE TABLE game (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    board INTEGER[] NOT NULL,
    is_player1_turn BOOLEAN NOT NULL,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE participation (
    id UUID PRIMARY KEY,
    player_id UUID NOT NULL REFERENCES player(id),
    game_id UUID NOT NULL REFERENCES game(id),
    is_player1 BOOLEAN NOT NULL,

    CONSTRAINT participation_game_player_unique UNIQUE (game_id, player_id),
    CONSTRAINT participation_player_turn_unique UNIQUE (game_id, is_player1)
)
