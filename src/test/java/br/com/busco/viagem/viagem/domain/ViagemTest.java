package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.sk.vo.Codigo;
import br.com.busco.viagem.sk.vo.Rota;
import br.com.busco.viagem.viagem.domain.exceptions.NaoPossivelIniciarViagemQueNaoSejaPendente;
import br.com.busco.viagem.viagem.domain.exceptions.ViagemIncompleta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViagemTest {

    private Viagem viagem;

    @Mock private Codigo codigo;
    @Mock private Rota rota;
    @Mock private PeriodoPlanejado periodoPlanejado;
    @Mock private MotoristaId motoristaId;
    @Mock private MonitorId monitorId;
    @Mock private VeiculoId veiculoId;
    @Mock private PlanejamentoId planejamentoId;
    @Mock private GrupoChecklistId checklistInicialId;
    @Mock private GrupoChecklistId checklistFinalId;

    @BeforeEach
    void setUp() {
        this.periodoPlanejado = PeriodoPlanejado.of(LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        viagem = Viagem.builder()
                .codigo(codigo)
                .rota(rota)
                .periodoPlanejado(periodoPlanejado)
                .motorista(motoristaId)
                .monitor(monitorId)
                .veiculo(veiculoId)
                .planejamento(planejamentoId)
                .checklistInicial(checklistInicialId)
                .checklistFinal(checklistFinalId)
                .build();
    }

    @Test
    void deveCriarViagemComStatusPendente() {
        // Assert
        assertThat(viagem.getStatus()).isEqualTo(Status.PENDENTE);
        assertThat(viagem.getPassageiros()).isEmpty();
        assertThat(viagem.getPeriodoReal()).isNull();
    }

    @Test
    void deveAlocarMotorista() {
        // Arrange
        MotoristaId novoMotorista = MotoristaId.randomId();

        // Act
        viagem.alocarMotorista(novoMotorista);

        // Assert
        assertThat(viagem.getMotorista()).isEqualTo(novoMotorista);
    }

    @Test
    void deveAlocarMonitor() {
        // Arrange
        MonitorId novoMonitor = MonitorId.randomId();

        // Act
        viagem.alocarMonitor(novoMonitor);

        // Assert
        assertThat(viagem.getMonitor()).isEqualTo(novoMonitor);
    }

    @Test
    void deveTrocarVeiculo() {
        // Arrange
        VeiculoId novoVeiculo = mock(VeiculoId.class);

        // Act
        viagem.trocarVeiculo(novoVeiculo);

        // Assert
        assertThat(viagem.getVeiculo()).isEqualTo(novoVeiculo);
    }

    @Test
    void deveTrocarRota() {
        // Arrange
        Rota novaRota = mock(Rota.class);

        // Act
        viagem.trocarRota(novaRota);

        // Assert
        assertThat(viagem.getRota()).isEqualTo(novaRota);
    }

    @Test
    void deveDesalocarMotorista() {
        // Act
        viagem.desalocarMotorista();

        // Assert
        assertThat(viagem.getMotorista()).isEqualTo(MotoristaId.VAZIO);
    }

    @Test
    void deveDesalocarMonitor() {
        // Act
        viagem.desalocarMonitor();

        // Assert
        assertThat(viagem.getMonitor()).isEqualTo(MonitorId.VAZIO);
    }

    @Test
    void deveTrocarMotorista() {
        // Arrange
        MotoristaId novoMotorista = mock(MotoristaId.class);

        // Act
        viagem.trocarMotorista(novoMotorista);

        // Assert
        assertThat(viagem.getMotorista()).isEqualTo(novoMotorista);
    }

    @Test
    void deveTrocarMonitor() {
        // Arrange
        MonitorId novoMonitor = mock(MonitorId.class);

        // Act
        viagem.trocarMonitor(novoMonitor);

        // Assert
        assertThat(viagem.getMonitor()).isEqualTo(novoMonitor);
    }

    @Test
    void deveAdicionarPassageiro() {
        // Arrange
        AlunoId alunoId = AlunoId.randomId();

        // Act
        viagem.adicionarPassageiro(alunoId);

        // Assert
        assertThat(viagem.getPassageiros()).containsExactly(alunoId);
    }

    @Test
    void naoDeveAdicionarPassageiroDuplicado() {
        // Arrange
        AlunoId alunoId = mock(AlunoId.class);
        viagem.adicionarPassageiro(alunoId);

        // Act & Assert
        assertThatThrownBy(() -> viagem.adicionarPassageiro(alunoId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Aluno já está na viagem");
    }

    @Test
    void deveAdicionarMultiplosPassageiros() {
        // Arrange
        Set<AlunoId> alunos = new HashSet<>();
        alunos.add(AlunoId.randomId());
        alunos.add(AlunoId.randomId());

        // Act
        viagem.adicionarPassageiros(alunos);

        // Assert
        assertThat(viagem.getPassageiros()).hasSize(2);
    }

    @Test
    void deveRemoverPassageiro() {
        // Arrange
        AlunoId alunoId = AlunoId.randomId();
        viagem.adicionarPassageiro(alunoId);

        // Act
        viagem.removerPassageiro(alunoId);

        // Assert
        assertThat(viagem.getPassageiros()).isEmpty();
    }

    @Test
    void naoDeveRemoverPassageiroInexistente() {
        // Arrange
        AlunoId alunoId = mock(AlunoId.class);

        // Act & Assert
        assertThatThrownBy(() -> viagem.removerPassageiro(alunoId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Aluno não está na viagem");
    }

    @Test
    void deveIniciarViagemQuandoPendenteECompleta() {
        // Act
        viagem.iniciar();

        // Assert
        assertThat(viagem.getStatus()).isEqualTo(Status.INICIADA);
        assertThat(viagem.getPeriodoReal()).isNotNull();
        assertThat(viagem.getPeriodoReal().getPartida()).isNotNull();
    }

    @Test
    void naoDeveIniciarViagemQuandoNaoPendente() {
        // Arrange
        viagem.iniciar(); // Muda status para INICIADA

        // Act & Assert
        assertThatThrownBy(() -> viagem.iniciar())
                .isInstanceOf(NaoPossivelIniciarViagemQueNaoSejaPendente.class);
    }

    @Test
    void naoDeveIniciarViagemSemMotorista() {
        // Arrange
        Viagem viagemSemMotorista = Viagem.builder()
                .codigo(codigo)
                .rota(rota)
                .periodoPlanejado(periodoPlanejado)
                .motorista(null)
                .monitor(monitorId)
                .veiculo(veiculoId)
                .build();

        // Act & Assert
        assertThatThrownBy(viagemSemMotorista::iniciar)
                .isInstanceOf(ViagemIncompleta.class);
    }

    @Test
    void naoDeveIniciarViagemSemVeiculo() {
        // Arrange
        Viagem viagemSemVeiculo = Viagem.builder()
                .codigo(codigo)
                .rota(rota)
                .periodoPlanejado(periodoPlanejado)
                .motorista(motoristaId)
                .monitor(monitorId)
                .veiculo(null)
                .build();

        // Act & Assert
        assertThatThrownBy(viagemSemVeiculo::iniciar)
                .isInstanceOf(ViagemIncompleta.class);
    }

    @Test
    void deveFinalizarViagem() {
        // Arrange
        viagem.iniciar();
        LocalDateTime chegadaPrevista = LocalDateTime.now().plusHours(2);
        when(periodoPlanejado.getChegada()).thenReturn(chegadaPrevista);

        // Act
        viagem.finalizar();

        // Assert
        assertThat(viagem.getStatus()).isEqualTo(Status.FINALIZADA);
        assertThat(viagem.getPeriodoReal().getChegada()).isNotNull();
    }

    @Test
    void deveCancelarViagemPendente() {
        // Act
        viagem.cancelar();

        // Assert
        assertThat(viagem.getStatus()).isEqualTo(Status.CANCELADA);
    }

    @Test
    void naoDeveCancelarViagemNaoPendente() {
        // Arrange
        viagem.iniciar();

        // Act & Assert
        assertThatThrownBy(() -> viagem.cancelar())
                .isInstanceOf(NaoPossivelIniciarViagemQueNaoSejaPendente.class);
    }

    @Test
    void deveRetornarViagemFormParaUpdate() {
        // Act
        ViagemForm form = viagem.update();

        // Assert
        assertThat(form).isNotNull();
    }
}