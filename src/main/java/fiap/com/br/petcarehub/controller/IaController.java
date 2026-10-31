package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.request.PerguntaIaRequest;
import fiap.com.br.petcarehub.dto.response.AnaliseIaResponse;
import fiap.com.br.petcarehub.service.AnaliseSaudeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets/{petId}/ia")
@RequiredArgsConstructor
public class IaController {

    private final AnaliseSaudeService service;

    @PostMapping("/analise")
    @Operation(summary = "Gerar análise de saúde com IA",
            description = "A IA interpreta os dados reais do pet (leituras IoT, alertas, consultas, score e plano "
                    + "preventivo) com apoio da base de conhecimento veterinária. Não recalcula regras e não "
                    + "substitui o veterinário. A análise fica gravada no histórico.")
    public ResponseEntity<AnaliseIaResponse> gerarAnalise(@PathVariable Long petId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarAnalise(petId));
    }

    @GetMapping("/analises")
    @Operation(summary = "Histórico de respostas da IA",
            description = "Últimas 20 análises, perguntas e explicações geradas pela IA para o pet, mais recentes primeiro.")
    public List<AnaliseIaResponse> historico(@PathVariable Long petId) {
        return service.historico(petId);
    }

    @PostMapping("/perguntar")
    @Operation(summary = "Perguntar à IA sobre o pet",
            description = "Responde perguntas sobre saúde, comportamento e cuidados do pet usando apenas os dados "
                    + "reais dele e a base veterinária. Mantém memória curta da conversa por usuário e pet. "
                    + "Não substitui o veterinário.")
    public AnaliseIaResponse perguntar(@PathVariable Long petId, @RequestBody @Valid PerguntaIaRequest request) {
        return service.perguntar(petId, request);
    }

    @PostMapping("/alertas/{alertaId}/explicar")
    @Operation(summary = "Explicar um alerta com IA",
            description = "Explica o que o alerta significa, qual regra do sistema o gerou, como se relaciona com os "
                    + "demais dados do pet e quando procurar o veterinário. Não substitui o veterinário.")
    public AnaliseIaResponse explicarAlerta(@PathVariable Long petId, @PathVariable Long alertaId) {
        return service.explicarAlerta(petId, alertaId);
    }
}
