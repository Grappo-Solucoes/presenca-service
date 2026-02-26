package br.com.busco.viagem.infra.listeners;

import br.com.busco.viagem.app.PresencaService;
import br.com.busco.viagem.app.cmd.CriarPresenca;
import br.com.busco.viagem.app.cmd.RegistrarFalta;
import br.com.busco.viagem.domain.events.ViagemCriada;
import br.com.busco.viagem.domain.events.ViagemFinalizada;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
@RabbitListener(
        queues = "${presenca-queue}",
        containerFactory = "rabbitListenerContainerFactory"
)
public class ViagemListener {

    private final PresencaService service;

    @RabbitHandler
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

    @RabbitHandler
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
