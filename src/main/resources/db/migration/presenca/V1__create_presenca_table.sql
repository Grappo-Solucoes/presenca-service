CREATE TABLE presenca (
                          id uuid NOT NULL,
                          aluno_id uuid NOT NULL,
                          viagem_id uuid NOT NULL,
                          data_viagem date not null,
                          status VARCHAR(20) NOT NULL,
                          justificativa TEXT,
                          version BIGINT,
                          tenant_id uuid not null,

                          PRIMARY KEY (id, data_viagem),
                          CONSTRAINT uk_presenca_aluno_viagem UNIQUE (tenant_id, aluno_id, viagem_id, data_viagem),
                          CONSTRAINT chk_status CHECK (
                              status IN ('PENDENTE', 'PRESENTE', 'FALTA', 'FALTA_JUSTIFICADA')
                              )
)
PARTITION BY RANGE (data_viagem) ;

