package br.com.busco.presenca.app;

import br.com.busco.presenca.domain.Viagem;
import br.com.busco.presenca.sk.ids.ViagemId;

public interface ViagemGateway {

    Viagem buscarViagem(ViagemId viagemId);

}
