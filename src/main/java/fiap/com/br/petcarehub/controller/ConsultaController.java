package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.ConsultaResponse;
import fiap.com.br.petcarehub.dto.PageResponse;
import fiap.com.br.petcarehub.entity.Consulta;
import fiap.com.br.petcarehub.service.ConsultaService;
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
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as consultas",
            description = "Retorna a lista completa de consultas cadastradas no sistema."
    )
    public List<ConsultaResponse> findAll() {

        return service.findAll()
                .stream()
                .map(c -> new ConsultaResponse(
                        c.getId(),
                        c.getPet().getNome(),
                        c.getClinica().getNome(),
                        c.getDataConsulta(),
                        c.getTipo(),
                        c.getDescricao(),
                        c.getDiagnostico(),
                        c.getValor(),
                        c.getRetornoRecomendado(),
                        c.getDataRetorno()
                ))
                .toList();
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar consultas paginadas", description = "Retorna a lista paginada de consultas, com ordenação e paginação controladas por Pageable.")
    public ResponseEntity<PageResponse<Consulta>> listarPaginado(
            @PageableDefault(size = 5, sort = "tipo", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                new PageResponse<>(service.getAllPaginado(pageable))
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar consulta por ID",
            description = "Retorna os dados da consulta correspondente ao identificador informado."
    )
    public ConsultaResponse findById(@PathVariable Long id) {

        Consulta c = service.findById(id);

        return new ConsultaResponse(
                c.getId(),
                c.getPet().getNome(),
                c.getClinica().getNome(),
                c.getDataConsulta(),
                c.getTipo(),
                c.getDescricao(),
                c.getDiagnostico(),
                c.getValor(),
                c.getRetornoRecomendado(),
                c.getDataRetorno()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar consulta",
            description = "Cadastra uma nova consulta com os dados enviados na requisição."
    )
    public ConsultaResponse add(
            @RequestBody @Valid Consulta consulta
    ) {

        Consulta salva = service.add(consulta);

        return new ConsultaResponse(
                salva.getId(),
                salva.getPet().getNome(),
                salva.getClinica().getNome(),
                salva.getDataConsulta(),
                salva.getTipo(),
                salva.getDescricao(),
                salva.getDiagnostico(),
                salva.getValor(),
                salva.getRetornoRecomendado(),
                salva.getDataRetorno()
        );
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar consulta",
            description = "Atualiza os dados da consulta correspondente ao identificador informado."
    )
    public ConsultaResponse update(
            @PathVariable Long id,
            @RequestBody @Valid Consulta consulta
    ) {

        Consulta atualizada = service.update(id, consulta);

        return new ConsultaResponse(
                atualizada.getId(),
                atualizada.getPet().getNome(),
                atualizada.getClinica().getNome(),
                atualizada.getDataConsulta(),
                atualizada.getTipo(),
                atualizada.getDescricao(),
                atualizada.getDiagnostico(),
                atualizada.getValor(),
                atualizada.getRetornoRecomendado(),
                atualizada.getDataRetorno()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover consulta", description = "Remove a consulta correspondente ao identificador informado.")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}