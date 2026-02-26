package br.com.busco.viagem.infra.viagem;

import br.com.busco.viagem.app.ViagemGateway;
import br.com.busco.viagem.domain.DataViagem;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.sk.ids.ViagemId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class ViagemGatewayImpl implements ViagemGateway {
    @Override
    public Viagem buscarViagem(ViagemId viagemId) {
        return Viagem.of(viagemId, DataViagem.of(LocalDateTime.now()));
    }
}
