package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.PresencaId;
import br.com.busco.viagem.sk.ids.ViagemId;

import java.util.List;
import java.util.Optional;

public interface PresencaRepository {

    Presenca save(Presenca presenca);
    Optional<Presenca> findById(PresencaId presenca);
    Optional<Presenca> findByAlunoAndViagemViagem(AlunoId aluno, ViagemId viagem);

    List<Presenca> findByViagemAndStatus(
            ViagemId viagemId,
            Status status
    );
}
