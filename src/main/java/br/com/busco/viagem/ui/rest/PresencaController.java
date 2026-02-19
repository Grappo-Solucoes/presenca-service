package br.com.busco.viagem.ui.rest;

import br.com.busco.viagem.app.PresencaService;
import br.com.busco.viagem.app.cmd.EmbarcarAluno;
import br.com.busco.viagem.app.cmd.RegistrarAusencia;
import br.com.busco.viagem.sk.ids.PresencaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/viagem")
@AllArgsConstructor
public class PresencaController {

    private final PresencaService service;

    @PatchMapping("/{id}/embarcar/{aluno}")
    @Operation(summary = "Embarcar aluno", description = "Embarcar aluno.")
    @ApiResponse(responseCode = "201", description = "Aluno embarcado com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> embarcar(@PathVariable UUID id, @PathVariable String aluno) throws Exception {
        EmbarcarAluno cmd = EmbarcarAluno.builder().id(ViagemId.fromString(id.toString())).aluno(aluno).build();
        service.handle(cmd);

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/").path(id.toString()).build().toUri())
                .build();
    }

    @PostMapping("/ausencia")
    @Operation(summary = "cadastra ausencia de Aluno", description ="cadastra ausencia de Aluno" )
    @ApiResponse(responseCode = "201", description = "Viagem editada com sucesso", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> cadastrarAusencia(@RequestBody @NonNull RegistrarAusencia cmd) throws Exception {
        PresencaId id = service.handle(cmd);

        return ResponseEntity.created(fromCurrentRequest()
                        .path("/rota").path(id.toString()).build().toUri())
                .build();
    }
}
