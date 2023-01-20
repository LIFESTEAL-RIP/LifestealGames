CREATE TABLE IF NOT EXISTS players
(
    id INT NOT NULL AUTO_INCREMENT,
    uuid CHAR(36) NOT NULL,
    cf_wins INT DEFAULT 0 NOT NULL,
    cf_losses INT DEFAULT 0 NOT NULL,
    cf_profit BIGINT DEFAULT 0 NOT NULL,
    rps_wins INT DEFAULT 0 NOT NULL,
    rps_losses INT DEFAULT 0 NOT NULL,
    rps_profit BIGINT DEFAULT 0 NOT NULL,
    crash_wins INT DEFAULT 0 NOT NULL,
    crash_losses INT DEFAULT 0 NOT NULL,
    crash_profit BIGINT DEFAULT 0 NOT NULL,
    PRIMARY KEY(id)
);