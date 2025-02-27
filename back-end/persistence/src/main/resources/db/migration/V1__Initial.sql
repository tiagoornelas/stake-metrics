CREATE TABLE fifa_bets
(
    id              BINARY(16) NOT NULL,
    is_paper_bet    BIT(1) NOT NULL,
    strategy_id     BINARY(16) NULL,
    match_id        BINARY(16) NULL,
    line            TINYINT NULL,
    handicap DOUBLE NULL,
    odds DOUBLE NULL,
    status          TINYINT NULL,
    profit DOUBLE NULL,
    bet_time        datetime NULL,
    odd_snapshot_id BINARY(16) NULL,
    CONSTRAINT pk_fifa_bets PRIMARY KEY (id)
);

CREATE TABLE fifa_leagues
(
    id             BINARY(16)   NOT NULL,
    integration_id BIGINT NOT NULL,
    status         TINYINT NULL,
    name           VARCHAR(255) NULL,
    link           VARCHAR(255) NULL,
    CONSTRAINT pk_fifa_leagues PRIMARY KEY (id)
);

CREATE TABLE fifa_matches
(
    id                       BINARY(16) NOT NULL,
    integration_id           BIGINT NOT NULL,
    time                     datetime NULL,
    status                   TINYINT NULL,
    league_id                BINARY(16) NULL,
    home_player_id           BINARY(16) NULL,
    away_player_id           BINARY(16) NULL,
    home_goals_at_half_time  INT NULL,
    home_goals_at_full_time  INT NULL,
    away_goals_at_half_time  INT NULL,
    away_goals_at_full_time  INT NULL,
    total_goals_at_half_time INT NULL,
    total_goals_at_full_time INT NULL,
    winner_player_id         BINARY(16) NULL,
    CONSTRAINT pk_fifa_matches PRIMARY KEY (id)
);

CREATE TABLE fifa_odd_snapshot
(
    id                         BINARY(16) NOT NULL,
    match_id                   BINARY(16) NULL,
    status                     TINYINT NULL,
    goals_handicap             DOUBLE NULL,
    over_goals_odd             DOUBLE NULL,
    under_goals_odd            DOUBLE NULL,
    home_odd                   DOUBLE NULL,
    draw_odd                   DOUBLE NULL,
    away_odd                   DOUBLE NULL,
    home_profit DOUBLE NULL,
    draw_profit DOUBLE NULL,
    away_profit DOUBLE NULL,
    over_profit DOUBLE NULL,
    under_profit DOUBLE NULL,
    match_odds_winner_sub_type TINYINT NULL,
    goal_line_winner_sub_type  TINYINT NULL,
    created_at                 datetime NULL,
    CONSTRAINT pk_fifa_odd_snapshot PRIMARY KEY (id)
);

CREATE TABLE fifa_odd_snapshot_trend_scope_analysis
(
    fifa_odd_snapshot_model_id BINARY(16) NOT NULL,
    trend_scope_analysis_id    BINARY(16) NOT NULL,
    CONSTRAINT pk_fifa_odd_snapshot_trendscopeanalysis PRIMARY KEY (fifa_odd_snapshot_model_id, trend_scope_analysis_id)
);

CREATE TABLE fifa_players
(
    id        BINARY(16)   NOT NULL,
    name      VARCHAR(255) NULL,
    league_id BINARY(16)   NULL,
    CONSTRAINT pk_fifa_players PRIMARY KEY (id)
);

CREATE TABLE fifa_strategies
(
    id          BINARY(16)   NOT NULL,
    status      TINYINT NULL,
    name        VARCHAR(255) NULL,
    market_type TINYINT NULL,
    user_id     BINARY(16)   NULL,
    CONSTRAINT pk_fifa_strategies PRIMARY KEY (id)
);

CREATE TABLE fifa_strategy_excluded_players
(
    player_id   BINARY(16) NOT NULL,
    strategy_id BINARY(16) NOT NULL,
    CONSTRAINT pk_fifa_strategy_excluded_players PRIMARY KEY (player_id, strategy_id)
);

CREATE TABLE fifa_strategy_leagues
(
    league_id   BINARY(16) NOT NULL,
    strategy_id BINARY(16) NOT NULL,
    CONSTRAINT pk_fifa_strategy_leagues PRIMARY KEY (league_id, strategy_id)
);

CREATE TABLE fifa_strategy_market_subtypes
(
    strategy_id    BINARY(16) NOT NULL,
    market_subtype TINYINT NULL
);

CREATE TABLE fifa_strategy_rules
(
    id                     BINARY(16) NOT NULL,
    type                   TINYINT NULL,
    value DOUBLE NOT NULL,
    fifa_strategy_scope_id BINARY(16) NULL,
    CONSTRAINT pk_fifa_strategy_rules PRIMARY KEY (id)
);

CREATE TABLE fifa_strategy_scopes
(
    id          BINARY(16) NOT NULL,
    matchup     TINYINT NULL,
    type        TINYINT NULL,
    strategy_id BINARY(16) NULL,
    CONSTRAINT pk_fifa_strategy_scopes PRIMARY KEY (id)
);

CREATE TABLE fifa_trend_scope_analysis
(
    id            BINARY(16) NOT NULL,
    matchup       TINYINT NULL,
    type          TINYINT NULL,
    total_matches INT NULL,
    home_player_probability DOUBLE NULL,
    home_player_fair_line DOUBLE NULL,
    home_player_juice DOUBLE NULL,
    draw_probability DOUBLE NULL,
    draw_fair_line DOUBLE NULL,
    draw_juice DOUBLE NULL,
    away_player_probability DOUBLE NULL,
    away_player_fair_line DOUBLE NULL,
    away_player_juice DOUBLE NULL,
    over_probability DOUBLE NULL,
    over_fair_line DOUBLE NULL,
    over_juice DOUBLE NULL,
    under_probability DOUBLE NULL,
    under_fair_line DOUBLE NULL,
    under_juice DOUBLE NULL,
    CONSTRAINT pk_fifa_trend_scope_analysis PRIMARY KEY (id)
);

CREATE TABLE messages
(
    id                     BINARY(16)   NOT NULL,
    messenger_chat_id      BINARY(16)   NULL,
    text                   VARCHAR(255) NULL,
    time                   datetime NULL,
    integration_message_id INT NULL,
    bet_id                 BINARY(16)   NULL,
    CONSTRAINT pk_messages PRIMARY KEY (id)
);

CREATE TABLE messenger_chats
(
    id                    BINARY(16)   NOT NULL,
    user_id               BINARY(16)   NULL,
    name                  VARCHAR(255) NULL,
    chat_id               VARCHAR(255) NULL,
    status                TINYINT NULL,
    delay                 INT    NOT NULL,
    delivery_probability DOUBLE NOT NULL,
    not_delivered_message VARCHAR(255) NULL,
    extra_text            VARCHAR(255) NULL,
    created_at            BIGINT NOT NULL,
    CONSTRAINT pk_messenger_chats PRIMARY KEY (id)
);

CREATE TABLE recovery_codes
(
    id          BINARY(16)   NOT NULL,
    code        VARCHAR(255) NULL,
    expire_date datetime NULL,
    user_id     BINARY(16)   NULL,
    CONSTRAINT pk_recovery_codes PRIMARY KEY (id)
);

CREATE TABLE subscriptions
(
    id             BINARY(16)   NOT NULL,
    user_id        BINARY(16)   NULL,
    integration_id VARCHAR(255) NULL,
    CONSTRAINT pk_subscriptions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              BINARY(16)   NOT NULL,
    email           VARCHAR(255) NULL,
    name            VARCHAR(255) NULL,
    password_hash   VARCHAR(255) NULL,
    type            TINYINT NULL,
    timezone_offset VARCHAR(6) NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_telegram_chats
(
    user_model_id     BINARY(16) NOT NULL,
    telegram_chats_id BINARY(16) NOT NULL,
    CONSTRAINT pk_users_telegram_chats PRIMARY KEY (user_model_id, telegram_chats_id)
);

ALTER TABLE fifa_leagues
    ADD CONSTRAINT uc_fifa_leagues_integration_id UNIQUE (integration_id);

ALTER TABLE fifa_players
    ADD CONSTRAINT uc_fifa_players_name UNIQUE (name);

ALTER TABLE fifa_matches
    ADD CONSTRAINT uc_fifa_matches_integration_id UNIQUE (integration_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE fifa_odd_snapshot_trend_scope_analysis
    ADD CONSTRAINT uc_fifa_odd_snapshot_trend_scope_analysis_trendscopeanalysis UNIQUE (trend_scope_analysis_id);

ALTER TABLE subscriptions
    ADD CONSTRAINT uc_subscriptions_user UNIQUE (user_id);

ALTER TABLE users_telegram_chats
    ADD CONSTRAINT uc_users_telegram_chats_telegramchats UNIQUE (telegram_chats_id);

CREATE INDEX idx_fifa_leagues_integration_id ON fifa_leagues (integration_id);

CREATE INDEX idx_fifa_matches_integration_id ON fifa_matches (integration_id);

ALTER TABLE fifa_bets
    ADD CONSTRAINT FK_FIFA_BETS_MATCH FOREIGN KEY (match_id) REFERENCES fifa_matches (id);

ALTER TABLE fifa_bets
    ADD CONSTRAINT FK_FIFA_BETS_STRATEGY FOREIGN KEY (strategy_id) REFERENCES fifa_strategies (id);

ALTER TABLE fifa_matches
    ADD CONSTRAINT FK_FIFA_MATCHES_AWAY_PLAYER FOREIGN KEY (away_player_id) REFERENCES fifa_players (id);

ALTER TABLE fifa_matches
    ADD CONSTRAINT FK_FIFA_MATCHES_HOME_PLAYER FOREIGN KEY (home_player_id) REFERENCES fifa_players (id);

ALTER TABLE fifa_matches
    ADD CONSTRAINT FK_FIFA_MATCHES_LEAGUE FOREIGN KEY (league_id) REFERENCES fifa_leagues (id);

ALTER TABLE fifa_matches
    ADD CONSTRAINT FK_FIFA_MATCHES_WINNER_PLAYER FOREIGN KEY (winner_player_id) REFERENCES fifa_players (id);

ALTER TABLE fifa_odd_snapshot
    ADD CONSTRAINT FK_FIFA_ODD_SNAPSHOT_MATCH FOREIGN KEY (match_id) REFERENCES fifa_matches (id);

ALTER TABLE fifa_players
    ADD CONSTRAINT FK_FIFA_PLAYERS_LEAGUE FOREIGN KEY (league_id) REFERENCES fifa_leagues (id);

ALTER TABLE fifa_strategies
    ADD CONSTRAINT FK_FIFA_STRATEGIES_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE fifa_strategy_rules
    ADD CONSTRAINT FK_FIFA_STRATEGY_RULES_SCOPE FOREIGN KEY (fifa_strategy_scope_id) REFERENCES fifa_strategy_scopes (id);

ALTER TABLE fifa_strategy_scopes
    ADD CONSTRAINT FK_FIFA_STRATEGY_SCOPES_STRATEGY FOREIGN KEY (strategy_id) REFERENCES fifa_strategies (id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_BET FOREIGN KEY (bet_id) REFERENCES fifa_bets (id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_MESSENGER_CHAT FOREIGN KEY (messenger_chat_id) REFERENCES messenger_chats (id);

ALTER TABLE messenger_chats
    ADD CONSTRAINT FK_MESSENGER_CHATS_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE recovery_codes
    ADD CONSTRAINT FK_RECOVERY_CODES_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE subscriptions
    ADD CONSTRAINT FK_SUBSCRIPTIONS_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE fifa_strategy_market_subtypes
    ADD CONSTRAINT FK_FIFA_STRATEGY_MARKET_SUBTYPES_STRATEGY FOREIGN KEY (strategy_id) REFERENCES fifa_strategies (id);

ALTER TABLE fifa_odd_snapshot_trend_scope_analysis
    ADD CONSTRAINT FK_FIFA_ODD_SNAPSHOT_TREND_SCOPE_ANALYSIS_SNAPSHOT FOREIGN KEY (fifa_odd_snapshot_model_id) REFERENCES fifa_odd_snapshot (id);

ALTER TABLE fifa_odd_snapshot_trend_scope_analysis
    ADD CONSTRAINT FK_FIFA_ODD_SNAPSHOT_TREND_SCOPE_ANALYSIS_TREND FOREIGN KEY (trend_scope_analysis_id) REFERENCES fifa_trend_scope_analysis (id);

ALTER TABLE fifa_strategy_excluded_players
    ADD CONSTRAINT FK_FIFA_STRATEGY_EXCLUDED_PLAYERS_PLAYER FOREIGN KEY (player_id) REFERENCES fifa_players (id);

ALTER TABLE fifa_strategy_excluded_players
    ADD CONSTRAINT FK_FIFA_STRATEGY_EXCLUDED_PLAYERS_STRATEGY FOREIGN KEY (strategy_id) REFERENCES fifa_strategies (id);

ALTER TABLE fifa_strategy_leagues
    ADD CONSTRAINT FK_FIFA_STRATEGY_LEAGUES_LEAGUE FOREIGN KEY (league_id) REFERENCES fifa_leagues (id);

ALTER TABLE fifa_strategy_leagues
    ADD CONSTRAINT FK_FIFA_STRATEGY_LEAGUES_STRATEGY FOREIGN KEY (strategy_id) REFERENCES fifa_strategies (id);

ALTER TABLE users_telegram_chats
    ADD CONSTRAINT FK_USERS_TELEGRAM_CHATS_TELEGRAM_CHAT FOREIGN KEY (telegram_chats_id) REFERENCES messenger_chats (id);

ALTER TABLE users_telegram_chats
    ADD CONSTRAINT FK_USERS_TELEGRAM_CHATS_USER FOREIGN KEY (user_model_id) REFERENCES users (id);
