package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.PageResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.service.ClinicaService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clinicas")
public class ClinicaController {

    private final ClinicaService service;

    public ClinicaController(ClinicaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as clínicas", description = "Retorna a lista completa de clínicas cadastradas no sistema.")
    public List<Clinica> findAll() {
        return service.findAll();
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar clínicas paginadas", description = "Retorna a lista paginada de clínicas, com ordenação e paginação controladas por Pageable.")
    public ResponseEntity<PageResponse<Clinica>> listarPaginado(
            @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                new PageResponse<>(service.getAllPaginado(pageable))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar clínica por ID", description = "Retorna os dados da clínica correspondente ao identificador informado.")
    public Clinica findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar clínica", description = "Cadastra uma nova clínica com os dados enviados na requisição.")
    public Clinica add(@RequestBody @Valid Clinica clinica) {
        return service.add(clinica);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar clínica", description = "Atualiza os dados da clínica correspondente ao identificador informado.")
    public Clinica update(
            @PathVariable Long id,
            @RequestBody @Valid Clinica clinica
    ) {
        return service.update(id, clinica);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover clínica", description = "Remove a clínica correspondente ao identificador informado.")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}