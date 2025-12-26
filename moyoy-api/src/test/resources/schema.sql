DROP TABLE IF EXISTS jwt_refresh_token;

CREATE TABLE jwt_refresh_token(
                                  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  user_id    BIGINT      NOT NULL,
                                  token_hash VARCHAR(44) NOT NULL,
                                  expires_at TIMESTAMP   NOT NULL,
                                  CONSTRAINT uq_token_hash UNIQUE (token_hash)
);