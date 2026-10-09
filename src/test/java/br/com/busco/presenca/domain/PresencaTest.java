package br.com.busco.presenca.domain;

import br.com.busco.presenca.domain.exceptions.NaoPossivelJustificarPresenca;
import br.com.busco.presenca.domain.policy.TransicaoDePresencaPolicy;
import br.com.busco.presenca.domain.service.PresencaDomainService;
import br.com.busco.presenca.sk.ids.AlunoId;
import br.com.busco.presenca.sk.ids.ViagemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PresencaTest {

    private AlunoId alunoId;
    private Viagem viagem;
    private Presenca presenca;
    private final PresencaDomainService domainService = new PresencaDomainService(new TransicaoDePresencaPolicy());

    @BeforeEach
    void setUp() {
        alunoId = AlunoId.fromString("123e4567-e89b-12d3-a456-426614174002");
        viagem = Viagem.of(
                ViagemId.fromString("22222222-2222-2222-2222-222222222222"),
                DataViagem.of(LocalDateTime.of(2026, 6, 10, 8, 0))
        );
        presenca = Presenca.of(alunoId, viagem);
    }

    @Test
    @DisplayName("cria presenca pendente")
    void criaPresencaPendente() {
        assertThat(presenca.getAluno()).isEqualTo(alunoId);
        assertThat(presenca.getViagem()).isEqualTo(viagem);
        assertThat(presenca.getStatus()).isEqualTo(Status.PENDENTE);
        assertThat(presenca.getJustificativa()).isNull();
    }

    @Test
    @DisplayName("registra presenca apenas quando pendente")
    void registraPresencaApenasQuandoPendente() {
        domainService.registrarPresenca(presenca);
        domainService.registrarPresenca(presenca);

        assertThat(presenca.getStatus()).isEqualTo(Status.PRESENTE);
        assertThat(presenca.getJustificativa()).isNull();
    }

    @Test
    @DisplayName("registra falta apenas quando pendente")
    void registraFaltaApenasQuandoPendente() {
        domainService.registrarFalta(presenca);
        domainService.registrarFalta(presenca);
        domainService.registrarPresenca(presenca);

        assertThat(presenca.getStatus()).isEqualTo(Status.FALTA);
        assertThat(presenca.getJustificativa()).isNull();
    }

    @Test
    @DisplayName("justifica falta pendente ou registrada")
    void justificaFaltaPendenteOuRegistrada() {
        domainService.registrarFalta(presenca);

        domainService.justificarFalta(presenca, "Atestado medico");

        assertThat(presenca.getStatus()).isEqualTo(Status.FALTA_JUSTIFICADA);
        assertThat(presenca.getJustificativa()).isEqualTo("Atestado medico");
    }

    @Test
    @DisplayName("mantem primeira justificativa quando ja justificada")
    void mantemPrimeiraJustificativaQuandoJaJustificada() {
        domainService.justificarFalta(presenca, "Primeiro motivo");

        domainService.justificarFalta(presenca, "Segundo motivo");

        assertThat(presenca.getStatus()).isEqualTo(Status.FALTA_JUSTIFICADA);
        assertThat(presenca.getJustificativa()).isEqualTo("Primeiro motivo");
    }

    @Test
    @DisplayName("nao justifica aluno presente")
    void naoJustificaAlunoPresente() {
        domainService.registrarPresenca(presenca);

        assertThatThrownBy(() -> domainService.justificarFalta(presenca, "Atestado medico"))
                .isInstanceOf(NaoPossivelJustificarPresenca.class)
                .hasMessage("Não pode justificar se já está presente");
        assertThat(presenca.getStatus()).isEqualTo(Status.PRESENTE);
        assertThat(presenca.getJustificativa()).isNull();
    }
}
