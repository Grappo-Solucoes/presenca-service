CREATE TABLE presenca (
                          id varchar(36) NOT NULL,
                          aluno_id varchar(36) NOT NULL,
                          viagem_id varchar(36) NOT NULL,
                          data_viagem date not null,
                          status VARCHAR(20) NOT NULL,
                          justificativa TEXT,
                          version BIGINT,
                          tenant_id varchar(36) not null,

                          PRIMARY KEY (id, data_viagem),
                          CONSTRAINT uk_presenca_aluno_viagem UNIQUE (tenant_id, aluno_id, viagem_id, data_viagem),
                          CONSTRAINT chk_status CHECK (
                              status IN ('PENDENTE', 'PRESENTE', 'FALTA', 'FALTA_JUSTIFICADA')
                              )
)
PARTITION BY RANGE (data_viagem) ;

