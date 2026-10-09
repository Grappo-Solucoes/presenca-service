package br.com.busco.presenca.domain.policy;

import br.com.busco.presenca.domain.Status;
import br.com.busco.presenca.domain.exceptions.NaoPossivelJustificarPresenca;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransicaoDePresencaPolicyTest {

    private final TransicaoDePresencaPolicy policy = new TransicaoDePresencaPolicy();

    @Test
    void presencaEFaltaSoValemAPartirDePendente() {
        for (Status status : Status.values()) {
            boolean pendente = status == Status.PENDENTE;
            assertThat(policy.podeRegistrarPresenca(status)).isEqualTo(pendente);
            assertThat(policy.podeRegistrarFalta(status)).isEqualTo(pendente);
        }
    }

    @Test
    void naoJustificaQuemEstaPresente() {
        assertThatThrownBy(() -> policy.validarJustificativa(Status.PRESENTE))
                .isInstanceOf(NaoPossivelJustificarPresenca.class);
    }

    @Test
    void justificaPendenteFaltaEJaJustificada() {
        assertThatCode(() -> policy.validarJustificativa(Status.PENDENTE)).doesNotThrowAnyException();
        assertThatCode(() -> policy.validarJustificativa(Status.FALTA)).doesNotThrowAnyException();
        assertThatCode(() -> policy.validarJustificativa(Status.FALTA_JUSTIFICADA)).doesNotThrowAnyException();
        assertThat(policy.jaEstaJustificada(Status.FALTA_JUSTIFICADA)).isTrue();
        assertThat(policy.jaEstaJustificada(Status.FALTA)).isFalse();
    }
}
