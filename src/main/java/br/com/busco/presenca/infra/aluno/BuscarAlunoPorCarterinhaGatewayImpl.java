package br.com.busco.presenca.infra.aluno;

import br.com.busco.presenca.app.BuscarAlunoPorCarterinhaGateway;
import br.com.busco.presenca.infra.aluno.repository.AlunoRepository;
import br.com.busco.presenca.sk.ids.AlunoId;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BuscarAlunoPorCarterinhaGatewayImpl implements BuscarAlunoPorCarterinhaGateway {

    private final AlunoRepository repository;
    private final RedisTemplate<String, AlunoId> redisTemplate;

    @Override
    public AlunoId buscarAlunoPorCarterinha(String carterinha) {
        return AlunoId.fromString("123e4567-e89b-12d3-a456-426614174002");
//        if (!StringUtils.hasText(carterinha)) {
//            return AlunoId.VAZIO;
//        }
//
//        String key = String.format("aluno:carterinha:%s", carterinha);
//        AlunoId cached = redisTemplate.opsForValue().get(key);
//        if (cached != null) {
//            return cached;
//        }
//        AlunoId aluno = repository.findIdByCarterinha(carterinha).orElse(AlunoId.VAZIO);
//        redisTemplate.opsForValue().set(key, aluno, 1, TimeUnit.HOURS);
//
//        return aluno;
    }
}
