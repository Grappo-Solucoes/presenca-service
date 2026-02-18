package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.domain.Presenca;
import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.PresencaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PresencaRegistrada implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private PresencaId id;
    private Instant occurredOn;

    public static PresencaRegistrada from(Presenca presenca) {
        return new PresencaRegistrada(presenca.getId(), Instant.now());
    }
}
