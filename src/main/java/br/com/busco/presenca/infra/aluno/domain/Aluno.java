package br.com.busco.presenca.infra.aluno.domain;

import br.com.busco.presenca.sk.ids.AlunoId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "aluno")
@NoArgsConstructor
public class Aluno {
    @EmbeddedId
    private AlunoId id;

    @Column(name = "carterinha")
    private String carterinha;

}