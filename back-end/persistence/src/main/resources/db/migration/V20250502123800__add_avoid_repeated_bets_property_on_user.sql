ALTER TABLE users
ADD COLUMN avoid_repeated_bets BOOLEAN NOT NULL DEFAULT FALSE;

-- This UPDATE is technically redundant but ensures consistency
UPDATE users SET avoid_repeated_bets = FALSE;