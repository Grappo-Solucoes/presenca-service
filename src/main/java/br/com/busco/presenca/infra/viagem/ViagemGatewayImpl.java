package br.com.busco.presenca.infra.viagem;

import br.com.busco.presenca.app.ViagemGateway;
import br.com.busco.presenca.domain.DataViagem;
import br.com.busco.presenca.domain.Viagem;
import br.com.busco.presenca.sk.ids.ViagemId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class ViagemGatewayImpl implements ViagemGateway {
    @Override
    public Viagem buscarViagem(ViagemId viagemId) {
        return Viagem.of(viagemId, DataViagem.of(LocalDateTime.now()));
    }
}
