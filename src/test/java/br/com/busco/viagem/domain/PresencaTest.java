package br.com.busco.viagem.domain;

import br.com.busco.viagem.domain.exceptions.NaoPossivelJustificarPresenca;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PresencaTest {

    private AlunoId alunoId;
    private ViagemId viagemId;
    private Presenca presenca;

    @BeforeEach
    void setUp() {
        alunoId = AlunoId.fromString(UUID.randomUUID().toString());
        viagemId = ViagemId.fromString(UUID.randomUUID().toString());
        presenca = Presenca.of(alunoId, viagemId);
    }

    @Test
    @DisplayName("Deve criar uma presença com status PENDENTE")
    void deveCriarPresencaComStatusPendente() {
        assertEquals(alunoId, presenca.getAluno());
        assertEquals(viagemId, presenca.getViagem());
        assertEquals(Status.PENDENTE, presenca.getStatus());
        assertNull(presenca.getJustificativa());
    }

    @Test
    @DisplayName("Deve registrar presença quando status é PENDENTE")
    void deveRegistrarPresencaQuandoPendente() {
        presenca.registrarPresenca();

        assertEquals(Status.PRESENTE, presenca.getStatus());
        assertNull(presenca.getJustificativa());
    }

    @Test
    @DisplayName("Não deve alterar status quando tentar registrar presença em status diferente de PENDENTE")
    void naoDeveAlterarStatusQuandoNaoPendentePresenca() {
        // Teste com status PRESENTE
        presenca.registrarPresenca();
        Status statusOriginal = presenca.getStatus();
        presenca.registrarPresenca();
        assertEquals(statusOriginal, presenca.getStatus());

        // Teste com status FALTA
        presenca = Presenca.of(alunoId, viagemId);
        presenca.registrarFalta();
        statusOriginal = presenca.getStatus();
        presenca.registrarPresenca();
        assertEquals(statusOriginal, presenca.getStatus());

        // Teste com status FALTA_JUSTIFICADA
        presenca = Presenca.of(alunoId, viagemId);
        presenca.justificarFalta("Motivo");
        statusOriginal = presenca.getStatus();
        presenca.registrarPresenca();
        assertEquals(statusOriginal, presenca.getStatus());
    }


    @Test
    @DisplayName("Deve registrar falta quando status é PENDENTE")
    void deveRegistrarFaltaQuandoPendente() {
        presenca.registrarFalta();

        assertEquals(Status.FALTA, presenca.getStatus());
        assertNull(presenca.getJustificativa());
    }


    @Test
    @DisplayName("Não deve alterar status quando tentar registrar falta em status diferente de PENDENTE")
    void naoDeveAlterarStatusQuandoNaoPendente() {
        // Teste com status PRESENTE
        presenca.registrarPresenca();
        Status statusOriginal = presenca.getStatus();
        presenca.registrarFalta();
        assertEquals(statusOriginal, presenca.getStatus());

        // Teste com status FALTA_JUSTIFICADA
        presenca = Presenca.of(alunoId, viagemId);
        presenca.justificarFalta("Motivo");
        statusOriginal = presenca.getStatus();
        presenca.registrarFalta();
        assertEquals(statusOriginal, presenca.getStatus());
    }

    @Test
    @DisplayName("Deve justificar falta quando status é PENDENTE")
    void deveJustificarFaltaQuandoPendente() {
        String motivo = "Motivo da falta";
        presenca.justificarFalta(motivo);

        assertEquals(Status.FALTA_JUSTIFICADA, presenca.getStatus());
        assertEquals(motivo, presenca.getJustificativa());
    }

    @Test
    @DisplayName("Deve justificar falta quando status é FALTA")
    void deveJustificarFaltaQuandoFalta() {
        presenca.registrarFalta();
        String motivo = "Justificativa para a falta";

        presenca.justificarFalta(motivo);

        assertEquals(Status.FALTA_JUSTIFICADA, presenca.getStatus());
        assertEquals(motivo, presenca.getJustificativa());
    }

    @Test
    @DisplayName("Deve manter status quando falta já está justificada")
    void deveManterStatusQuandoFaltaJaJustificada() {
        String motivoOriginal = "Motivo original";
        presenca.justificarFalta(motivoOriginal);
        Status statusOriginal = presenca.getStatus();
        String justificativaOriginal = presenca.getJustificativa();

        presenca.justificarFalta("Novo motivo");

        assertEquals(statusOriginal, presenca.getStatus());
        assertEquals(justificativaOriginal, presenca.getJustificativa());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar justificar presença com status PRESENTE")
    void deveLancarExcecaoQuandoPresente() {
        presenca.registrarPresenca();

        assertThrows(NaoPossivelJustificarPresenca.class,
                () -> presenca.justificarFalta("Motivo"));
    }

    @Test
    @DisplayName("Deve permitir múltiplas operações em sequência")
    void devePermitirMultiplasOperacoesEmSequencia() {
        // Criar presença com status PENDENTE
        assertEquals(Status.PENDENTE, presenca.getStatus());

        // Marcar falta
        presenca.registrarFalta();
        assertEquals(Status.FALTA, presenca.getStatus());

        // Justificar falta
        presenca.justificarFalta("Motivo válido");
        assertEquals(Status.FALTA_JUSTIFICADA, presenca.getStatus());
        assertEquals("Motivo válido", presenca.getJustificativa());

        // Tentar marcar presença (não deve alterar)
        presenca.registrarPresenca();
        assertEquals(Status.FALTA_JUSTIFICADA, presenca.getStatus());

        // Tentar justificar novamente (não deve alterar)
        presenca.justificarFalta("Outro motivo");
        assertEquals("Motivo válido", presenca.getJustificativa());
    }

    @Test
    @DisplayName("Deve permitir justificativa com motivo vazio ou em branco")
    void devePermitirJustificativaComMotivoVazio() {
        presenca.justificarFalta("");
        assertEquals(Status.FALTA_JUSTIFICADA, presenca.getStatus());
        assertEquals("", presenca.getJustificativa());

        presenca = Presenca.of(alunoId, viagemId);
        presenca.justificarFalta("   ");
        assertEquals(Status.FALTA_JUSTIFICADA, presenca.getStatus());
        assertEquals("   ", presenca.getJustificativa());
    }

    @Test
    @DisplayName("Deve manter integridade dos IDs")
    void deveManterIntegridadeDosIds() {
        AlunoId novoAlunoId = AlunoId.fromString(UUID.randomUUID().toString());
        ViagemId novaViagemId = ViagemId.fromString(UUID.randomUUID().toString());

        Presenca novaPresenca = Presenca.of(novoAlunoId, novaViagemId);

        assertEquals(novoAlunoId, novaPresenca.getAluno());
        assertEquals(novaViagemId, novaPresenca.getViagem());

        // Verificar que os IDs são diferentes da presença anterior
        assertNotEquals(alunoId, novoAlunoId);
        assertNotEquals(viagemId, novaViagemId);
    }

}