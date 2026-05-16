package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.service.ResponsavelService;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<Responsavel>> listarPaginado(
            @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllProdutosPaginado(pageable));
    }

    @GetMapping("/{id}")
    public Responsavel findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Responsavel add(@RequestBody Responsavel responsavel) {
        return service.add(responsavel);
    }

    @PutMapping("/{id}")
    public Responsavel update(
            @PathVariable Long id,
            @RequestBody Responsavel responsavel
    ) {
        return service.update(id, responsavel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}