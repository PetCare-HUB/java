package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.PageResponse;
import fiap.com.br.petcarehub.dto.request.ConsultaRequest;
import fiap.com.br.petcarehub.dto.response.ConsultaResponse;
import fiap.com.br.petcarehub.service.ConsultaService;
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
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService service;

    @GetMapping
    @Operation(summary = "Listar consultas")
    public PageResponse<ConsultaResponse> listar(@PageableDefault(size = 10, sort = "dataConsulta", direction = Sort.Direction.DESC) Pageable pageable) {
        return new PageResponse<>(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID")
    public ConsultaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Criar consulta")
    public ResponseEntity<ConsultaResponse> criar(@RequestBody @Valid ConsultaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar consulta")
    public ConsultaResponse atualizar(@PathVariable Long id, @RequestBody @Valid ConsultaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir consulta")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Buscar consultas por pet")
    public PageResponse<ConsultaResponse> buscarPorPet(@PathVariable Long petId, Pageable pageable) {
        return new PageResponse<>(service.buscarPorPet(petId, pageable));
    }
}
