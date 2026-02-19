CREATE TABLE presenca (
                          id VARCHAR(36) NOT NULL,
                          aluno_id VARCHAR(36) NOT NULL,
                          viagem_id VARCHAR(36) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          justificativa TEXT,
                          version BIGINT,

                          PRIMARY KEY (id),
                          UNIQUE KEY uk_presenca_aluno_viagem (aluno_id, viagem_id),
                          INDEX idx_status (status),
                          INDEX idx_viagem (viagem_id),
                          INDEX idx_aluno (aluno_id),

                          CONSTRAINT chk_status CHECK (status IN ('PENDENTE', 'PRESENTE', 'FALTA', 'FALTA_JUSTIFICADA'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;