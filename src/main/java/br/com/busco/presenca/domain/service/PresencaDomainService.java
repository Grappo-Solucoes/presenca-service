package br.com.busco.presenca.domain.service;

import br.com.busco.presenca.domain.Presenca;
import br.com.busco.presenca.domain.policy.TransicaoDePresencaPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Domain Service: consulta a policy antes de o agregado mudar de status. */
@Component
@RequiredArgsConstructor
public class PresencaDomainService {

    private final TransicaoDePresencaPolicy transicaoPolicy;

    public void registrarPresenca(Presenca presenca) {
        if (transicaoPolicy.podeRegistrarPresenca(presenca.getStatus())) {
            presenca.registrarPresenca();
        }
    }

    public void registrarFalta(Presenca presenca) {
        if (transicaoPolicy.podeRegistrarFalta(presenca.getStatus())) {
            presenca.registrarFalta();
        }
    }

    public void justificarFalta(Presenca presenca, String motivo) {
        transicaoPolicy.validarJustificativa(presenca.getStatus());
        if (transicaoPolicy.jaEstaJustificada(presenca.getStatus())) {
            return;
        }
        presenca.justificarFalta(motivo);
    }
}
