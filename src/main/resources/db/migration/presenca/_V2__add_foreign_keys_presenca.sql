ALTER TABLE presenca
    ADD CONSTRAINT fk_presenca_aluno
        FOREIGN KEY (aluno_id)
            REFERENCES aluno(id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE presenca
    ADD CONSTRAINT fk_presenca_viagem
        FOREIGN KEY (viagem_id)
            REFERENCES viagem(id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;