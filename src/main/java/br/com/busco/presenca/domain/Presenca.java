package br.com.busco.presenca.domain;

import br.com.busco.presenca.domain.events.FaltaJustificada;
import br.com.busco.presenca.domain.events.FaltaRegistrada;
import br.com.busco.presenca.domain.events.PresencaCriada;
import br.com.busco.presenca.domain.events.PresencaRegistrada;
import br.com.busco.presenca.domain.exceptions.NaoPossivelJustificarPresenca;
import br.com.busco.presenca.sk.ddd.AbstractAggregateRoot;
import br.com.busco.presenca.sk.ids.AlunoId;
import br.com.busco.presenca.sk.ids.PresencaId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static br.com.busco.presenca.sk.ids.PresencaId.randomId;

@Table
@Entity
@Getter
@EqualsAndHashCode(of = {"aluno", "viagem"}, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public final class Presenca extends AbstractAggregateRoot<PresencaId> {

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "aluno_id"))
    private AlunoId aluno;

    @Embedded
    private Viagem viagem;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String justificativa;

    private Presenca(AlunoId aluno, Viagem viagem) {
        super(randomId());
        this.aluno = aluno;
        this.viagem = viagem;
        this.status = Status.PENDENTE;
        this.registerEvent(PresencaCriada.from(this));
    }

    public void registrarPresenca() {
        if (status != Status.PENDENTE) {
            return;
        }
        this.status = Status.PRESENTE;
        registerEvent(PresencaRegistrada.from(this));
    }

    public void registrarFalta() {
        if (status != Status.PENDENTE) return;
        this.status = Status.FALTA;
        registerEvent(FaltaRegistrada.from(this));
    }

    public void justificarFalta(String motivo) {

        if (status == Status.PRESENTE) {
            throw new NaoPossivelJustificarPresenca();
        }

        if (status == Status.FALTA_JUSTIFICADA) return;

        if (status == Status.PENDENTE) {
            this.status = Status.FALTA_JUSTIFICADA;
        }

        if (status == Status.FALTA) {
            this.status = Status.FALTA_JUSTIFICADA;
        }

        this.justificativa = motivo;

        registerEvent(FaltaJustificada.from(this));
    }


    public static Presenca of(AlunoId aluno, Viagem viagem) {
        return new Presenca(aluno, viagem);
    }
}
