package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.PageResponse;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import fiap.com.br.petcarehub.projection.PetSummary;
import fiap.com.br.petcarehub.service.PetService;
import jakarta.validation.Valid;
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
    public List<Pet> findAll() {
        return service.findAll();
    }

    @GetMapping("/paginado")
    public ResponseEntity<PageResponse<Pet>> listarPaginado(
            @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                new PageResponse<>(service.getAllPaginado(pageable))
        );
    }


    @GetMapping("/{id}")
    public Pet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet add(@RequestBody @Valid Pet pet) {
        return service.add(pet);
    }

    @PutMapping("/{id}")
    public Pet update(
            @PathVariable Long id,
            @RequestBody @Valid Pet pet
    ) {
        return service.update(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/nome")
    public PageResponse<PetSummary> buscarPorNome(
            @RequestParam String nome,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getByNome(nome, pageable)
        );
    }

    @GetMapping("/especie")
    public PageResponse<PetSummary> buscarPorEspecie(
            @RequestParam EspeciePet especie,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getByEspecie(especie, pageable)
        );
    }


    @GetMapping("/raca")
    public PageResponse<PetSummary> buscarPorRaca(
            @RequestParam String raca,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getByRaca(raca, pageable)
        );
    }

    @GetMapping("/sexo")
    public PageResponse<PetSummary> buscarPorSexo(
            @RequestParam SexoPet sexo,
            Pageable pageable
    ) {

        return new PageResponse<>(
                service.getBySexo(sexo, pageable)
        );
    }
}