package br.com.busco.presenca.app.cmd;

import br.com.busco.presenca.sk.ids.ViagemId;
import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmbarcarAluno {
    private ViagemId id;
    private String aluno;
}