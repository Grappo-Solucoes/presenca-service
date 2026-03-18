package br.com.busco.presenca.domain.events;

import br.com.busco.presenca.domain.Presenca;
import br.com.busco.presenca.sk.ddd.DomainEvent;
import br.com.busco.presenca.sk.ids.PresencaId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PresencaCriada implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private PresencaId id;
    private Instant occurredOn;

    public static PresencaCriada from(Presenca presenca) {
        return new PresencaCriada(presenca.getId(), Instant.now());
    }
}
