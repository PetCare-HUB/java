package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.PageResponse;
import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.projection.ResponsavelSummary;
import fiap.com.br.petcarehub.service.ResponsavelService;
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
@RequestMapping("/responsaveis")
public class ResponsavelController {

    private final ResponsavelService service;

    public ResponsavelController(ResponsavelService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os responsáveis", description = "Retorna a lista completa de responsáveis cadastrados no sistema.")
    public List<Responsavel> findAll() {
        return service.findAll();
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar responsáveis paginados", description = "Retorna a lista paginada de responsáveis, com ordenação e paginação controladas por Pageable.")
    public ResponseEntity<PageResponse<Responsavel>> listarPaginado(
            @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                new PageResponse<>(service.getAllPaginado(pageable))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar responsável por ID", description = "Retorna os dados do responsável correspondente ao identificador informado.")
    public Responsavel findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar responsável", description = "Cadastra um novo responsável com os dados enviados na requisição.")
    public Responsavel add(@RequestBody @Valid Responsavel responsavel) {
        return service.add(responsavel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar responsável", description = "Atualiza os dados do responsável correspondente ao identificador informado.")
    public Responsavel update(
            @PathVariable Long id,
            @RequestBody @Valid Responsavel responsavel
    ) {
        return service.update(id, responsavel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover responsável", description = "Remove o responsável correspondente ao identificador informado.")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    @GetMapping("nome")
    @Operation(summary = "Buscar responsáveis por nome", description = "Retorna a lista de responsáveis filtrada pelo nome informado.")
    public PageResponse<ResponsavelSummary> buscarPorNome(
            @RequestParam String nome,
            Pageable pageable) {
        return new PageResponse<>(
                service.getByNome(
                        nome,
                        pageable
                )
        );
    }

    @GetMapping("/email")
    @Operation(summary = "Buscar responsáveis por email", description = "Retorna a lista de responsáveis filtrada pelo email informado.")
    public PageResponse<ResponsavelSummary> buscarPorEmail(

            @RequestParam String email,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getByEmail(
                        email,
                        pageable
                )
        );
    }

    @GetMapping("cpf")
    @Operation(summary = "Buscar responsáveis por CPF", description = "Retorna a lista de responsáveis filtrada pelo CPF informado.")
    public PageResponse<ResponsavelSummary> buscarPorCpf(
            @RequestParam String cpf,
            Pageable pageable) {
        return new PageResponse<>(
                service.getByCpf(
                        cpf,
                        pageable
                )
        );
    }

    @GetMapping("/telefone")
    @Operation(summary = "Buscar responsáveis por telefone", description = "Retorna a lista de responsáveis filtrada pelo telefone informado.")
    public PageResponse<ResponsavelSummary> buscarPorTelefone(
            @RequestParam String telefone,
            Pageable pageable) {
        return new PageResponse<>(
                service.getByTelefone(
                        telefone,
                        pageable
                )
        );
    }
}