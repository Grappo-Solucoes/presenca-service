package br.com.busco.presenca.app;

import br.com.busco.presenca.app.cmd.CriarPresenca;
import br.com.busco.presenca.app.cmd.EmbarcarAluno;
import br.com.busco.presenca.app.cmd.RegistrarAusencia;
import br.com.busco.presenca.app.cmd.RegistrarFalta;
import br.com.busco.presenca.domain.DataViagem;
import br.com.busco.presenca.domain.Presenca;
import br.com.busco.presenca.domain.PresencaRepository;
import br.com.busco.presenca.domain.Status;
import br.com.busco.presenca.domain.Viagem;
import br.com.busco.presenca.domain.exceptions.NaoPossivelJustificarPresenca;
import br.com.busco.presenca.domain.policy.TransicaoDePresencaPolicy;
import br.com.busco.presenca.domain.service.PresencaDomainService;
import br.com.busco.presenca.infra.redis.EmbarcarRateLimiter;
import br.com.busco.presenca.infra.redis.PresencaLockService;
import br.com.busco.presenca.infra.redis.PresencaCacheService;
import br.com.busco.presenca.sk.ids.AlunoId;
import br.com.busco.presenca.sk.ids.ViagemId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresencaServiceTest {

    private static final AlunoId ALUNO = AlunoId.fromString("123e4567-e89b-12d3-a456-426614174002");
    private static final ViagemId VIAGEM_ID = ViagemId.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private PresencaRepository repository;

    @Mock
    private BuscarAlunoPorCarterinhaGateway carterinhaGateway;

    @Mock
    private ViagemGateway viagemGateway;

    @Mock
    private PresencaCacheService cacheService;

    @Mock
    private EmbarcarRateLimiter rateLimiter;

    @Mock
    private PresencaLockService lockService;

    private PresencaService service;
    private Viagem viagem;

    @BeforeEach
    void setUp() {
        service = new PresencaService(repository, carterinhaGateway, viagemGateway, cacheService, rateLimiter, lockService,
                new PresencaDomainService(new TransicaoDePresencaPolicy()));
        viagem = Viagem.of(VIAGEM_ID, DataViagem.of(LocalDateTime.of(2026, 6, 10, 8, 0)));
    }

    @Test
    @DisplayName("cria presenca pendente a partir de evento de viagem")
    void criaPresencaPendente() {
        when(viagemGateway.buscarViagem(VIAGEM_ID)).thenReturn(viagem);
        when(repository.save(any(Presenca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.handle(CriarPresenca.builder().id(VIAGEM_ID).aluno(ALUNO).build());

        ArgumentCaptor<Presenca> captor = ArgumentCaptor.forClass(Presenca.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getAluno()).isEqualTo(ALUNO);
        assertThat(captor.getValue().getViagem()).isEqualTo(viagem);
        assertThat(captor.getValue().getStatus()).isEqualTo(Status.PENDENTE);
    }

    @Test
    @DisplayName("embarca aluno pendente e atualiza cache")
    void embarcaAlunoPendente() {
        Presenca presenca = Presenca.of(ALUNO, viagem);
        when(carterinhaGateway.buscarAlunoPorCarterinha("CAR123")).thenReturn(ALUNO);
        when(rateLimiter.podeEmbarcar(ALUNO)).thenReturn(true);
        when(lockService.tryLock(ALUNO, VIAGEM_ID)).thenReturn(true);
        when(cacheService.getPresencaFromCache(ALUNO, VIAGEM_ID)).thenReturn(Optional.empty());
        when(repository.findByAlunoAndViagemViagem(ALUNO, VIAGEM_ID)).thenReturn(Optional.of(presenca));
        when(repository.save(presenca)).thenReturn(presenca);

        service.handle(EmbarcarAluno.builder().id(VIAGEM_ID).aluno("CAR123").build());

        assertThat(presenca.getStatus()).isEqualTo(Status.PRESENTE);
        verify(cacheService).cachePresenca(presenca);
        verify(lockService).unlock(ALUNO, VIAGEM_ID);
    }

    @Test
    @DisplayName("bloqueia embarque por rate limit")
    void bloqueiaEmbarquePorRateLimit() {
        when(carterinhaGateway.buscarAlunoPorCarterinha("CAR123")).thenReturn(ALUNO);
        when(rateLimiter.podeEmbarcar(ALUNO)).thenReturn(false);

        assertThatThrownBy(() -> service.handle(EmbarcarAluno.builder().id(VIAGEM_ID).aluno("CAR123").build()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Limite de tentativas excedido");

        verify(lockService, never()).tryLock(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("bloqueia embarque quando lock esta ocupado")
    void bloqueiaEmbarqueQuandoLockEstaOcupado() {
        when(carterinhaGateway.buscarAlunoPorCarterinha("CAR123")).thenReturn(ALUNO);
        when(rateLimiter.podeEmbarcar(ALUNO)).thenReturn(true);
        when(lockService.tryLock(ALUNO, VIAGEM_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.handle(EmbarcarAluno.builder().id(VIAGEM_ID).aluno("CAR123").build()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Operação em andamento");
    }

    @Test
    @DisplayName("registra falta de aluno pendente")
    void registraFaltaDeAlunoPendente() {
        Presenca presenca = Presenca.of(ALUNO, viagem);
        when(lockService.tryLock(ALUNO, VIAGEM_ID)).thenReturn(true);
        when(repository.findByAlunoAndViagemViagem(ALUNO, VIAGEM_ID)).thenReturn(Optional.of(presenca));
        when(repository.save(presenca)).thenReturn(presenca);

        service.handle(RegistrarFalta.builder().id(VIAGEM_ID).aluno(ALUNO).build());

        assertThat(presenca.getStatus()).isEqualTo(Status.FALTA);
        verify(lockService).unlock(ALUNO, VIAGEM_ID);
    }

    @Test
    @DisplayName("justifica falta de aluno pendente")
    void justificaFaltaDeAlunoPendente() {
        Presenca presenca = Presenca.of(ALUNO, viagem);
        when(lockService.tryLock(ALUNO, VIAGEM_ID)).thenReturn(true);
        when(repository.findByAlunoAndViagemViagem(ALUNO, VIAGEM_ID)).thenReturn(Optional.of(presenca));
        when(repository.save(presenca)).thenReturn(presenca);

        service.handle(RegistrarAusencia.builder()
                .id(VIAGEM_ID)
                .aluno(ALUNO)
                .motivo("Atestado medico")
                .build());

        assertThat(presenca.getStatus()).isEqualTo(Status.FALTA_JUSTIFICADA);
        assertThat(presenca.getJustificativa()).isEqualTo("Atestado medico");
        verify(lockService).unlock(ALUNO, VIAGEM_ID);
    }

    @Test
    @DisplayName("nao justifica aluno presente")
    void naoJustificaAlunoPresente() {
        Presenca presenca = Presenca.of(ALUNO, viagem);
        new PresencaDomainService(new TransicaoDePresencaPolicy()).registrarPresenca(presenca);
        when(lockService.tryLock(ALUNO, VIAGEM_ID)).thenReturn(true);
        when(repository.findByAlunoAndViagemViagem(ALUNO, VIAGEM_ID)).thenReturn(Optional.of(presenca));

        assertThatThrownBy(() -> service.handle(RegistrarAusencia.builder()
                .id(VIAGEM_ID)
                .aluno(ALUNO)
                .motivo("Atestado medico")
                .build()))
                .isInstanceOf(NaoPossivelJustificarPresenca.class);

        verify(repository, never()).save(presenca);
        verify(lockService).unlock(ALUNO, VIAGEM_ID);
    }

    @Test
    @DisplayName("falha quando presenca nao existe")
    void falhaQuandoPresencaNaoExiste() {
        when(carterinhaGateway.buscarAlunoPorCarterinha("CAR123")).thenReturn(ALUNO);
        when(rateLimiter.podeEmbarcar(ALUNO)).thenReturn(true);
        when(lockService.tryLock(ALUNO, VIAGEM_ID)).thenReturn(true);
        when(cacheService.getPresencaFromCache(ALUNO, VIAGEM_ID)).thenReturn(Optional.empty());
        when(repository.findByAlunoAndViagemViagem(ALUNO, VIAGEM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.handle(EmbarcarAluno.builder().id(VIAGEM_ID).aluno("CAR123").build()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Presença não encontrada");

        verify(lockService).unlock(ALUNO, VIAGEM_ID);
    }
}
