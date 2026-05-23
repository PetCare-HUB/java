package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.PageResponse;
import fiap.com.br.petcarehub.dto.request.AlertaSaudeRequest;
import fiap.com.br.petcarehub.dto.response.AlertaSaudeResponse;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import fiap.com.br.petcarehub.service.AlertaSaudeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaSaudeController {

    private final AlertaSaudeService service;

    @GetMapping
    @Operation(summary = "Buscar alertas", description = "Filtra alertas por pet, tipo e status de resolução.")
    public PageResponse<AlertaSaudeResponse> buscar(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) TipoAlerta tipo,
            @RequestParam(required = false) Boolean resolvido,
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PageResponse<>(service.buscar(petId, tipo, resolvido, pageable));
    }

    @PostMapping
    @Operation(summary = "Criar alerta manual")
    public ResponseEntity<AlertaSaudeResponse> criar(@RequestBody @Valid AlertaSaudeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}/resolver")
    @Operation(summary = "Resolver alerta")
    public AlertaSaudeResponse resolver(@PathVariable Long id) {
        return service.resolver(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir alerta")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
