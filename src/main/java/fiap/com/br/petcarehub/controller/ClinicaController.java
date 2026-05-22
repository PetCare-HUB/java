package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.PageResponse;
import fiap.com.br.petcarehub.dto.request.ClinicaRequest;
import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.dto.response.PetResponse;
import fiap.com.br.petcarehub.service.ClinicaService;
import fiap.com.br.petcarehub.service.PetService;
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
@RequestMapping("/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService service;
    private final PetService petService;

    @GetMapping
    @Operation(summary = "Listar clínicas", description = "Lista clínicas cadastradas para vínculo com pets e consultas. O dashboard completo fica na API .NET.")
    public PageResponse<ClinicaResponse> listar(@PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PageResponse<>(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar clínica por ID")
    public ClinicaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
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

    @GetMapping("/nome")
    @Operation(summary = "Buscar clínicas por nome")
    public PageResponse<ClinicaResponse> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return new PageResponse<>(service.buscarPorNome(nome, pageable));
    }

    @GetMapping("/{id}/pets-em-risco")
    @Operation(summary = "Listar pets em risco de uma clínica", description = "Endpoint de consulta para o dashboard B2B consumir dados processados pela API Java.")
    public PageResponse<PetResponse> petsEmRisco(
            @PathVariable Long id,
            @RequestParam(defaultValue = "vermelho") String nivel,
            @PageableDefault(size = 10, sort = "scoreAtual", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return new PageResponse<>(petService.petsEmRisco(id, nivel, pageable));
    }
}
