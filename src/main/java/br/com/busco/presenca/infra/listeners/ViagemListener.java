package br.com.busco.presenca.infra.listeners;

import br.com.busco.presenca.app.PresencaService;
import br.com.busco.presenca.app.cmd.CriarPresenca;
import br.com.busco.presenca.app.cmd.RegistrarFalta;
import br.com.busco.presenca.domain.events.ViagemCriada;
import br.com.busco.presenca.domain.events.ViagemFinalizada;
import br.com.busco.presenca.sk.ids.AlunoId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

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
