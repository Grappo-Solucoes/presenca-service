package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViagemCriada implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private Set<AlunoId> passageiros;
    private Instant occurredOn;
}

