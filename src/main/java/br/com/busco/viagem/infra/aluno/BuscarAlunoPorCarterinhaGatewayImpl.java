package br.com.busco.viagem.infra.aluno;

import br.com.busco.viagem.app.BuscarAlunoPorCarterinhaGateway;
import br.com.busco.viagem.infra.aluno.repository.AlunoRepository;
import br.com.busco.viagem.sk.ids.AlunoId;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class BuscarAlunoPorCarterinhaGatewayImpl implements BuscarAlunoPorCarterinhaGateway {

    private final AlunoRepository repository;
    private final RedisTemplate<String, AlunoId> redisTemplate;

    @Override
    public AlunoId buscarAlunoPorCarterinha(String carterinha) {

        if (!StringUtils.hasText(carterinha)) {
            return AlunoId.VAZIO;
        }

        String key = String.format("aluno:carterinha:%s", carterinha);
        AlunoId cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        AlunoId aluno = repository.findIdByCarterinha(carterinha).orElse(AlunoId.VAZIO);
        redisTemplate.opsForValue().set(key, aluno, 1, TimeUnit.HOURS);

        return aluno;
    }
}
