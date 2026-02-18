package br.com.busco.viagem.app.cmd;

import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarFalta {
    private ViagemId id;
    private AlunoId aluno;
}