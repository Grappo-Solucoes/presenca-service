package br.com.busco.viagem.infra.aluno.repository;

import br.com.busco.viagem.infra.aluno.domain.Aluno;
import br.com.busco.viagem.sk.ids.AlunoId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends CrudRepository<Aluno, AlunoId> {
    Optional<AlunoId> findIdByCarterinha(String carteirinha);
}
