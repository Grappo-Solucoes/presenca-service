package br.com.busco.viagem.infra.redis;

import br.com.busco.viagem.domain.Presenca;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PresencaCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long CACHE_TTL = 30; // minutos

    public Optional<Presenca> getPresencaFromCache(AlunoId aluno, ViagemId viagem) {
        String key = generateKey(aluno, viagem);
        return Optional.ofNullable((Presenca) redisTemplate.opsForValue().get(key));
    }

    public void cachePresenca(Presenca presenca) {
        String key = generateKey(presenca.getAluno(), presenca.getViagem());
        redisTemplate.opsForValue().set(key, presenca, CACHE_TTL, TimeUnit.MINUTES);
    }

    public void evictCache(AlunoId aluno, ViagemId viagem) {
        redisTemplate.delete(generateKey(aluno, viagem));
    }

    private String generateKey(AlunoId aluno, ViagemId viagem) {
        return String.format("presenca:%s:%s", aluno.toUUID(), viagem.toUUID());
    }
}