package br.com.busco.presenca.domain.policy;

import br.com.busco.presenca.domain.Status;
import br.com.busco.presenca.domain.exceptions.NaoPossivelJustificarPresenca;
import org.springframework.stereotype.Component;

/**
 * Regra de negócio fora do agregado: o que cada status permite.
 * Presença e falta só valem a partir de PENDENTE; justificar vale para tudo, menos para quem já está presente.
 */
@Component
public class TransicaoDePresencaPolicy {

    public boolean podeRegistrarPresenca(Status atual) {
        return atual == Status.PENDENTE;
    }

    public boolean podeRegistrarFalta(Status atual) {
        return atual == Status.PENDENTE;
    }

    public boolean jaEstaJustificada(Status atual) {
        return atual == Status.FALTA_JUSTIFICADA;
    }

    public void validarJustificativa(Status atual) {
        if (atual == Status.PRESENTE) {
            throw new NaoPossivelJustificarPresenca();
        }
    }
}
