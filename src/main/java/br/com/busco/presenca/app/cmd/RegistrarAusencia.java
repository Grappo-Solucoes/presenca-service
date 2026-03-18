package br.com.busco.presenca.app.cmd;

import br.com.busco.presenca.sk.ids.AlunoId;
import br.com.busco.presenca.sk.ids.ViagemId;
import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarAusencia {
    private String motivo;
    private ViagemId id;
    private AlunoId aluno;

}