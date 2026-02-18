package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.domain.Presenca;
import br.com.busco.viagem.domain.PresencaRepository;
import br.com.busco.viagem.sk.ids.PresencaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresencaRepositoryImpl extends JpaRepository<Presenca, PresencaId>, PresencaRepository {
}
