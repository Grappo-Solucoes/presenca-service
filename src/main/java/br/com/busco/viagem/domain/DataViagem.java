package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"dataViagem"})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class DataViagem implements ValueObject {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataViagem;

    public static DataViagem of(LocalDateTime dataViagem) {
        if (isNull(dataViagem)) {
//            throw new PlanejamentoPeriodoSemDatasPrevistas();
        }
        return new DataViagem(dataViagem);
    }

}
