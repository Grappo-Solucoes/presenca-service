package br.com.busco.viagem.app;

import br.com.busco.viagem.app.cmd.CriarPresenca;
import br.com.busco.viagem.app.cmd.EmbarcarAluno;
import br.com.busco.viagem.app.cmd.RegistrarAusencia;
import br.com.busco.viagem.app.cmd.RegistrarFalta;
import br.com.busco.viagem.domain.Presenca;
import br.com.busco.viagem.domain.PresencaRepository;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.domain.service.EmbarcarRateLimiter;
import br.com.busco.viagem.domain.service.PresencaLockService;
import br.com.busco.viagem.infra.redis.PresencaCacheService;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.PresencaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static jakarta.persistence.LockModeType.PESSIMISTIC_READ;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@AllArgsConstructor

@Log
@Service
@Validated
@Transactional(propagation = REQUIRES_NEW)
public class PresencaService {

    private final PresencaRepository repository;
    private final BuscarAlunoPorCarterinhaGateway carterinhaGateway;
    private final ViagemGateway viagemGateway;
    private final PresencaCacheService cacheService;
    private final EmbarcarRateLimiter rateLimiter;
    private final PresencaLockService lockService;

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(CriarPresenca cmd) {
        Viagem viagem = viagemGateway.buscarViagem(cmd.getId());
        Presenca presenca = Presenca.of(cmd.getAluno(), viagem);
        return repository.save(presenca).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(EmbarcarAluno cmd) {

        ViagemId viagem = cmd.getId();
        AlunoId aluno = carterinhaGateway.buscarAlunoPorCarterinha(cmd.getAluno());
        if (!rateLimiter.podeEmbarcar(aluno)) {
            throw new IllegalStateException("Limite de tentativas excedido");
        }

        if (!lockService.tryLock(aluno, viagem)) {
            throw new IllegalStateException("Operação em andamento");
        }

        try {
            Presenca presenca = cacheService.getPresencaFromCache(aluno, viagem)
                    .orElseGet(() -> repository.findByAlunoAndViagemViagem(aluno, viagem)
                            .orElseThrow(() -> new EntityNotFoundException("Presença não encontrada")));

            presenca.registrarPresenca();
            Presenca saved = repository.save(presenca);

            // Atualiza cache
            cacheService.cachePresenca(saved);

            return saved.getId();

        } finally {
            lockService.unlock(aluno, viagem);
        }
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(RegistrarAusencia cmd) {
        ViagemId viagem = cmd.getId();
        AlunoId aluno = cmd.getAluno();
        if (!lockService.tryLock(aluno, viagem)) {
            throw new IllegalStateException(
                    "Falta sendo processada por outro usuário"
            );
        }

        try {
            Presenca presenca = repository.findByAlunoAndViagemViagem(aluno, viagem)
                    .orElseThrow(() -> new EntityNotFoundException("Presença não encontrada"));

            presenca.justificarFalta(cmd.getMotivo());
            return repository.save(presenca).getId();

        } finally {
            lockService.unlock(aluno, viagem);
        }
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(RegistrarFalta cmd) {
        ViagemId viagem = cmd.getId();
        AlunoId aluno = cmd.getAluno();

        if (!lockService.tryLock(aluno, viagem)) {
            throw new IllegalStateException(
                    "Falta sendo registrada por outro processo"
            );
        }

        try {
            Presenca presenca = repository.findByAlunoAndViagemViagem(aluno, viagem)
                    .orElseThrow(() -> new EntityNotFoundException("Presença não encontrada"));

            presenca.registrarFalta();
            return repository.save(presenca).getId();
        } finally {
            lockService.unlock(aluno, viagem);
        }
    }
}
