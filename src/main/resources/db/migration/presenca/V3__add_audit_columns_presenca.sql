ALTER TABLE presenca
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_presenca_created_at ON presenca(created_at);
CREATE INDEX idx_presenca_updated_at ON presenca(updated_at);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS trigger AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trg_presenca_updated_at
    BEFORE UPDATE ON presenca
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();