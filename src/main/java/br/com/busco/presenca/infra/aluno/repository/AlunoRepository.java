package br.com.busco.presenca.infra.aluno.repository;

import br.com.busco.presenca.infra.aluno.domain.Aluno;
import br.com.busco.presenca.sk.ids.AlunoId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends CrudRepository<Aluno, AlunoId> {
    @Query("SELECT a.id FROM Aluno a WHERE a.carterinha = :carteirinha")
    Optional<AlunoId> findIdByCarterinha(String carteirinha);
}
