package br.com.busco.viagem.app;

import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;

public interface ViagemGateway {

    Viagem buscarViagem(ViagemId viagemId);

}
