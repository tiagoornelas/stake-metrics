-- Add bet365_id column to fifa_matches table
ALTER TABLE fifa_matches
    ADD COLUMN bet365_id BIGINT NULL;
