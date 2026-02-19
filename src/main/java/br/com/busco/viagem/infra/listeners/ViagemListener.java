package br.com.busco.viagem.infra.listeners;

import br.com.busco.viagem.app.PresencaService;
import br.com.busco.viagem.app.cmd.CriarPresenca;
import br.com.busco.viagem.app.cmd.RegistrarFalta;
import br.com.busco.viagem.infra.listeners.events.ViagemCriada;
import br.com.busco.viagem.infra.listeners.events.ViagemFinalizada;
import br.com.busco.viagem.sk.ids.AlunoId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class ViagemListener {

    private final PresencaService service;

    @RabbitListener(queues = "${rabbitmq.queue.viagem:viagem-queue}")
    public void on(@Payload ViagemCriada evt) {
        Set<AlunoId> alunos = evt.getPassageiros();
        for (AlunoId aluno : alunos) {
            CriarPresenca cmd = CriarPresenca.builder()
                    .id(evt.getId())
                    .aluno(aluno)
                    .build();
            service.handle(cmd);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.viagem:viagem-queue}")
    public void on(@Payload ViagemFinalizada evt) {
        Set<AlunoId> alunos = evt.getPassageiros();
        for (AlunoId aluno : alunos) {
            RegistrarFalta cmd = RegistrarFalta.builder()
                    .id(evt.getId())
                    .aluno(aluno)
                    .build();
            service.handle(cmd);
        }
    }
}
