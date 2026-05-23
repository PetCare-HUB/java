package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.PageResponse;
import fiap.com.br.petcarehub.dto.request.PetRequest;
import fiap.com.br.petcarehub.dto.response.*;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;
    private final ScoreSaudeService scoreSaudeService;
    private final AlertaSaudeService alertaSaudeService;
    private final LeituraIotService leituraIotService;
    private final EventoPreventivoService eventoPreventivoService;
    private final TimelineService timelineService;

    @GetMapping
    @Operation(summary = "Listar pets", description = "Lista pets com paginação e ordenação.")
    public PageResponse<PetResponse> listar(@PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PageResponse<>(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por ID")
    public PetResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Criar pet")
    public ResponseEntity<PetResponse> criar(@RequestBody @Valid PetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pet")
    public PetResponse atualizar(@PathVariable Long id, @RequestBody @Valid PetRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir pet")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/nome")
    @Operation(summary = "Buscar pets por nome")
    public PageResponse<PetResponse> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return new PageResponse<>(service.buscarPorNome(nome, pageable));
    }

    @GetMapping("/busca")
    @Operation(summary = "Busca combinada de pets", description = "Busca por espécie, raça, clínica e faixa de score.")
    public PageResponse<PetResponse> buscarComFiltros(
            @RequestParam(required = false) EspeciePet especie,
            @RequestParam(required = false) String raca,
            @RequestParam(required = false) Long clinicaId,
            @RequestParam(required = false) Integer scoreMin,
            @RequestParam(required = false) Integer scoreMax,
            @PageableDefault(size = 10, sort = "scoreAtual", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return new PageResponse<>(service.buscarComFiltros(especie, raca, clinicaId, scoreMin, scoreMax, pageable));
    }

    @GetMapping("/{id}/score-saude")
    @Operation(summary = "Consultar score atual do pet")
    public ScoreSaudeResponse scoreAtual(@PathVariable Long id) {
        return scoreSaudeService.scoreAtual(id);
    }

    @PostMapping("/{id}/score-saude/calcular")
    @Operation(summary = "Recalcular score de saúde", description = "Calcula o score usando últimas leituras IoT, alertas ativos e histórico clínico.")
    public ScoreSaudeResponse calcularScore(@PathVariable Long id) {
        return scoreSaudeService.calcular(id);
    }

    @GetMapping("/{id}/score-saude/historico")
    @Operation(summary = "Histórico de score do pet")
    public List<ScoreSaudeResponse> historicoScore(@PathVariable Long id) {
        return scoreSaudeService.historico(id);
    }

    @GetMapping("/{id}/alertas/ativos")
    @Operation(summary = "Alertas ativos do pet")
    public List<AlertaSaudeResponse> alertasAtivos(@PathVariable Long id) {
        return alertaSaudeService.alertasAtivosDoPet(id);
    }

    @GetMapping("/{id}/leituras/coleira")
    @Operation(summary = "Histórico de leituras da coleira")
    public List<LeituraColeiraResponse> leiturasColeira(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate
    ) {
        return leituraIotService.buscarColeira(id, de, ate);
    }

    @GetMapping("/{id}/leituras/comedouro")
    @Operation(summary = "Histórico de leituras do comedouro")
    public List<LeituraComedouroResponse> leiturasComedouro(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate
    ) {
        return leituraIotService.buscarComedouro(id, de, ate);
    }

    @GetMapping("/{id}/leituras/ambiente")
    @Operation(summary = "Histórico de leituras ambientais")
    public List<LeituraAmbienteResponse> leiturasAmbiente(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate
    ) {
        return leituraIotService.buscarAmbiente(id, de, ate);
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Timeline longitudinal do pet", description = "Consolida consultas, leituras IoT, alertas, scores e eventos preventivos.")
    public TimelinePetResponse timeline(@PathVariable Long id) {
        return timelineService.timeline(id);
    }

    @GetMapping("/{id}/plano-preventivo")
    @Operation(summary = "Plano preventivo do pet")
    public List<EventoPreventivoResponse> planoPreventivo(@PathVariable Long id) {
        return eventoPreventivoService.planoDoPet(id);
    }
}