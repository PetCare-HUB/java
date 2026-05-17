package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.PageResponse;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import fiap.com.br.petcarehub.projection.PetSummary;
import fiap.com.br.petcarehub.service.PetService;
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
@RequestMapping("/pets")
public class PetController {

    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os pets", description = "Retorna a lista completa de pets cadastrados no sistema.")
    public List<Pet> findAll() {
        return service.findAll();
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar pets paginados", description = "Retorna a lista paginada de pets, com ordenação e paginação controladas por Pageable.")
    public ResponseEntity<PageResponse<Pet>> listarPaginado(
            @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                new PageResponse<>(service.getAllPaginado(pageable))
        );
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por ID", description = "Retorna os dados do pet correspondente ao identificador informado.")
    public Pet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar pet", description = "Cadastra um novo pet com os dados enviados na requisição.")
    public Pet add(@RequestBody @Valid Pet pet) {
        return service.add(pet);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pet", description = "Atualiza os dados do pet correspondente ao identificador informado.")
    public Pet update(
            @PathVariable Long id,
            @RequestBody @Valid Pet pet
    ) {
        return service.update(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover pet", description = "Remove o pet correspondente ao identificador informado.")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/nome")
    @Operation(summary = "Buscar pets por nome", description = "Retorna a lista de pets filtrada pelo nome informado.")
    public PageResponse<PetSummary> buscarPorNome(
            @RequestParam String nome,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getByNome(nome, pageable)
        );
    }

    @GetMapping("/especie")
    @Operation(summary = "Buscar pets por espécie", description = "Retorna a lista de pets filtrada pela espécie informada.")
    public PageResponse<PetSummary> buscarPorEspecie(
            @RequestParam EspeciePet especie,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getByEspecie(especie, pageable)
        );
    }


    @GetMapping("/raca")
    @Operation(summary = "Buscar pets por raça", description = "Retorna a lista de pets filtrada pela raça informada.")
    public PageResponse<PetSummary> buscarPorRaca(
            @RequestParam String raca,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getByRaca(raca, pageable)
        );
    }

    @GetMapping("/sexo")
    @Operation(summary = "Buscar pets por sexo", description = "Retorna a lista de pets filtrada pelo sexo informado.")
    public PageResponse<PetSummary> buscarPorSexo(
            @RequestParam SexoPet sexo,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getBySexo(sexo, pageable)
        );
    }
}