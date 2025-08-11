ALTER TABLE players
DROP CONSTRAINT IF EXISTS players_team_id_fkey,
  ADD  CONSTRAINT players_team_id_fkey
  FOREIGN KEY (team_id) REFERENCES teams(id)
  ON DELETE CASCADE;

ALTER TABLE matches
DROP CONSTRAINT IF EXISTS matches_home_team_id_fkey,
  ADD  CONSTRAINT matches_home_team_id_fkey
  FOREIGN KEY (home_team_id) REFERENCES teams(id)
  ON DELETE CASCADE;

ALTER TABLE matches
DROP CONSTRAINT IF EXISTS matches_away_team_id_fkey,
  ADD  CONSTRAINT matches_away_team_id_fkey
  FOREIGN KEY (away_team_id) REFERENCES teams(id)
  ON DELETE CASCADE;
