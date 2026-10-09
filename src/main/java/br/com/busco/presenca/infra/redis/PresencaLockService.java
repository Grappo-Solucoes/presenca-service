package br.com.busco.presenca.infra.redis;

import br.com.busco.presenca.sk.ids.AlunoId;
import br.com.busco.presenca.sk.ids.ViagemId;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class PresencaLockService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(AlunoId aluno, ViagemId viagem) {
        String key = String.format("lock:presenca:%s:%s",
                aluno.toUUID(), viagem.toUUID());

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, "LOCKED", Duration.ofSeconds(10));

        return Boolean.TRUE.equals(acquired);
    }

    public void unlock(AlunoId aluno, ViagemId viagem) {
        String key = String.format("lock:presenca:%s:%s",
                aluno.toUUID(), viagem.toUUID());
        redisTemplate.delete(key);
    }
}