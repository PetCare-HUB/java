package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.PageResponse;
import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.projection.ResponsavelSummary;
import fiap.com.br.petcarehub.service.ResponsavelService;
import jakarta.validation.Valid;
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
    public List<Responsavel> findAll() {
        return service.findAll();
    }

    @GetMapping("/paginado")
    public ResponseEntity<PageResponse<Responsavel>> listarPaginado(
            @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                new PageResponse<>(service.getAllPaginado(pageable))
        );
    }

    @GetMapping("/{id}")
    public Responsavel findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Responsavel add(@RequestBody @Valid Responsavel responsavel) {
        return service.add(responsavel);
    }

    @PutMapping("/{id}")
    public Responsavel update(
            @PathVariable Long id,
            @RequestBody @Valid Responsavel responsavel
    ) {
        return service.update(id, responsavel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    @GetMapping("nome")
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