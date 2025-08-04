CREATE TABLE IF NOT EXISTS users
(
    id         SERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    role       VARCHAR(30)  NOT NULL CHECK (role IN ('admin', 'user'))
    );

CREATE TABLE IF NOT EXISTS seasons
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    start_date DATE        NOT NULL,
    end_date   DATE        NOT NULL
    );

CREATE TABLE IF NOT EXISTS teams
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS players
(
    id         SERIAL PRIMARY KEY,
    team_id    INT REFERENCES teams (id),
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    position   VARCHAR(30),
    age        INT,
    height     INT,
    weight     INT
    );

CREATE TABLE IF NOT EXISTS matches
(
    id              SERIAL PRIMARY KEY,
    season_id       INT  NOT NULL REFERENCES seasons (id),
    date            DATE NOT NULL,
    home_team_id    INT  NOT NULL REFERENCES teams (id),
    away_team_id    INT  NOT NULL REFERENCES teams (id),
    home_team_score INT DEFAULT 0,
    away_team_score INT DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS match_participants
(
    id        SERIAL PRIMARY KEY,
    match_id  INT   NOT NULL REFERENCES matches (id) ON DELETE CASCADE,
    player_id INT   NOT NULL REFERENCES players (id),
    team_id   INT   NOT NULL REFERENCES teams (id),
    stats     JSONB NOT NULL DEFAULT '{}'::JSONB,
    UNIQUE (match_id, player_id)
    );
