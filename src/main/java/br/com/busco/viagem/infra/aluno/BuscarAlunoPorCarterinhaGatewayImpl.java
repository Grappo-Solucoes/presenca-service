package br.com.busco.viagem.infra.aluno;

import br.com.busco.viagem.app.BuscarAlunoPorCarterinhaGateway;
import br.com.busco.viagem.infra.aluno.repository.AlunoRepository;
import br.com.busco.viagem.sk.ids.AlunoId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class BuscarAlunoPorCarterinhaGatewayImpl implements BuscarAlunoPorCarterinhaGateway {

    private final AlunoRepository repository;

    @Override
    public AlunoId buscarAlunoPorCarterinha(String carterinha) {
        if (!StringUtils.hasText(carterinha)) {
            return AlunoId.VAZIO;
        }
        return repository.findIdByCarterinha(carterinha).orElse(AlunoId.VAZIO);
    }
}
