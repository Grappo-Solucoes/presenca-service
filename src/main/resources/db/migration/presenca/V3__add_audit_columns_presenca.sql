ALTER TABLE presenca
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

CREATE INDEX idx_presenca_created_at ON presenca(created_at);
CREATE INDEX idx_presenca_updated_at ON presenca(updated_at);