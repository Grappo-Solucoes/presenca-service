package br.com.busco.viagem.domain.service;

import br.com.busco.viagem.sk.ids.AlunoId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmbarcarRateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean podeEmbarcar(AlunoId aluno) {
        String key = String.format("embarque:aluno:%s", aluno.toUUID());
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        // Máximo de 1 embarque por minuto por aluno
        return count <= 1;
    }
}