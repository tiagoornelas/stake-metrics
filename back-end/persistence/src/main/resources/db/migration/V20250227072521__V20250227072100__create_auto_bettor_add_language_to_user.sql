CREATE TABLE auto_bettor (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) UNIQUE,
    integration_id VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 0,
    created_at datetime NOT NULL,
    CONSTRAINT fk_auto_bettor_user FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE users
    ADD COLUMN language TINYINT NOT NULL DEFAULT 0;

UPDATE users SET language = 0 WHERE language IS NULL;

CREATE INDEX idx_auto_bettor_user ON auto_bettor(user_id);
CREATE INDEX idx_auto_bettor_integration ON auto_bettor(integration_id);