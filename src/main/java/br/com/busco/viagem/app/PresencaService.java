package br.com.busco.viagem.app;

import br.com.busco.viagem.app.cmd.CriarPresenca;
import br.com.busco.viagem.app.cmd.EmbarcarAluno;
import br.com.busco.viagem.app.cmd.RegistrarAusencia;
import br.com.busco.viagem.app.cmd.RegistrarFalta;
import br.com.busco.viagem.domain.Presenca;
import br.com.busco.viagem.domain.PresencaRepository;
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
import static java.lang.String.format;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@AllArgsConstructor

@Log
@Service
@Validated
@Transactional(propagation = REQUIRES_NEW)
public class PresencaService {

    private final PresencaRepository repository;
    private final BuscarAlunoPorCarterinhaGateway carterinhaGateway;

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(CriarPresenca cmd) {
        Presenca presenca = Presenca.of(cmd.getAluno(), cmd.getId());
        return repository.save(presenca).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(EmbarcarAluno cmd) {
        ViagemId viagem = cmd.getId();
        AlunoId aluno = carterinhaGateway.buscarAlunoPorCarterinha(cmd.getAluno());

        Presenca presenca = repository.findByAlunoAndViagem(aluno, viagem)
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Account with code.")));
        presenca.registrarPresenca();

        return repository.save(presenca).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(RegistrarAusencia cmd) {
        ViagemId viagem = cmd.getId();
        AlunoId aluno = cmd.getAluno();

        Presenca presenca = repository.findByAlunoAndViagem(aluno, viagem)
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Account with code.")));
        presenca.justificarFalta(cmd.getMotivo());

        return repository.save(presenca).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PresencaId handle(RegistrarFalta cmd) {
        ViagemId viagem = cmd.getId();
        AlunoId aluno = cmd.getAluno();

        Presenca presenca = repository.findByAlunoAndViagem(aluno, viagem)
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Account with code.")));
        presenca.registrarFalta();

        return repository.save(presenca).getId();
    }
}
