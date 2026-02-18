package br.com.busco.viagem.app.cmd;

import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CriarPresenca {
    private ViagemId id;
    private AlunoId aluno;
}