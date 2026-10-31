package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.StatusConhecimentoResponse;
import fiap.com.br.petcarehub.dto.response.TrechoConhecimentoResponse;
import fiap.com.br.petcarehub.service.BaseConhecimentoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Administração da base de conhecimento da IA. Fica sob /admin, que o
 * SecurityConfig já restringe a ROLE_ADMIN.
 */
@RestController
@RequestMapping("/admin/ia")
@RequiredArgsConstructor
public class IaAdminController {

    private final BaseConhecimentoService service;

    @PostMapping("/reindexar")
    @Operation(summary = "Reindexar a base de conhecimento",
            description = "Relê os arquivos .md da base veterinária, gera os embeddings no Google Gemini e substitui "
                    + "todos os trechos indexados. Se o Gemini estiver indisponível, responde 503 e a base antiga é "
                    + "mantida. Necessário após trocar o modelo de embedding.")
    public StatusConhecimentoResponse reindexar() {
        return service.reindexar();
    }

    @GetMapping("/conhecimento/status")
    @Operation(summary = "Status da base de conhecimento",
            description = "Quantidade de trechos indexados, arquivos de origem, data da última indexação e modelo de embedding.")
    public StatusConhecimentoResponse status() {
        return service.status();
    }

    @GetMapping("/conhecimento")
    @Operation(summary = "Buscar na base de conhecimento",
            description = "Busca semântica direta (sem o modelo de chat): devolve os trechos que o RAG usaria "
                    + "para a pergunta, com a similaridade. Útil para conferir se a base cobre um assunto.")
    public List<TrechoConhecimentoResponse> buscar(
            @RequestParam String q,
            @RequestParam(defaultValue = "4") int topK) {
        return service.buscar(q, topK);
    }
}
