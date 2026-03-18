package br.com.busco.presenca.infra.repo;

import br.com.busco.presenca.domain.Presenca;
import br.com.busco.presenca.domain.PresencaRepository;
import br.com.busco.presenca.sk.ids.PresencaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresencaRepositoryImpl extends JpaRepository<Presenca, PresencaId>, PresencaRepository {
}
