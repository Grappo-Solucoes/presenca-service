package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"viagem", "dataViagem"})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class Viagem implements ValueObject {

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "viagem_id"))
    private ViagemId viagem;

    @Embedded
    @AttributeOverride(name = "partida", column = @Column(name = "data_viagem"))
    private DataViagem dataViagem;

    public static Viagem of(ViagemId viagem, DataViagem dataViagem) {
        return new Viagem(viagem, dataViagem);
    }
}
