package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.PageResponse;
import fiap.com.br.petcarehub.dto.request.ClinicaRequest;
import fiap.com.br.petcarehub.dto.response.AlertaSaudeResponse;
import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.dto.response.PetResponse;
import fiap.com.br.petcarehub.service.AlertaSaudeService;
import fiap.com.br.petcarehub.service.ClinicaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService service;
    private final AlertaSaudeService alertaSaudeService;

    @GetMapping
    @Operation(summary = "Listar clínicas", description = "Lista clínicas com paginação e ordenação.")
    public PageResponse<ClinicaResponse> listar(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PageResponse<>(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar clínica por ID")
    public ClinicaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/nome")
    @Operation(summary = "Buscar clínicas por nome")
    public PageResponse<ClinicaResponse> buscarPorNome(
            @RequestParam String nome,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PageResponse<>(service.buscarPorNome(nome, pageable));
    }

    @GetMapping("/{id}/pets-em-risco")
    @Operation(summary = "Pets em risco", description = "Lista pets da clínica com score crítico ou em alerta")
    public ResponseEntity<List<PetResponse>> petEmRisco(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "vermelho") String nivel) {
        return ResponseEntity.ok(service.petEmRisco(id, nivel));
    }

    @GetMapping("/{id}/metricas")
    @Operation(summary = "Métricas da clínica", description = "Retorna métricas de pets, consultas, alertas e score da clínica")
    public ResponseEntity<Object> metricas(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "90d") String periodo) {
        return ResponseEntity.ok(service.metricas(id, periodo));
    }

    @GetMapping("/{id}/agenda/proximos-30-dias")
    @Operation(summary = "Agenda da clínica", description = "Retorna consultas agendadas para os próximos 30 dias")
    public ResponseEntity<Object> agendaProximos30Dias(@PathVariable Long id) {
        return ResponseEntity.ok(service.agendaProximos30Dias(id));
    }

    @GetMapping("/{id}/alertas-iot/hoje")
    @Operation(summary = "Alertas IoT de hoje", description = "Retorna alertas gerados por sensores IoT para hoje")
    public ResponseEntity<List<AlertaSaudeResponse>> alertasIotHoje(@PathVariable Long id) {
        return ResponseEntity.ok(service.alertasIotHoje(id));
    }

    @PostMapping
    @Operation(summary = "Criar clínica")
    public ResponseEntity<ClinicaResponse> criar(@RequestBody @Valid ClinicaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar clínica")
    public ClinicaResponse atualizar(@PathVariable Long id, @RequestBody @Valid ClinicaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir clínica")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}