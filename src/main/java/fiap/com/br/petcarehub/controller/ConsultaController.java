package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.entity.Consulta;
import fiap.com.br.petcarehub.service.ConsultaService;
import org.springframework.data.domain.Page;
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
    public List<Consulta> findAll() {
        return service.findAll();
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<Consulta>> listarPaginado(
            @PageableDefault(size = 5, sort = "tipo", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllProdutosPaginado(pageable));
    }

    @GetMapping("/{id}")
    public Consulta findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta add(@RequestBody Consulta consulta) {
        return service.add(consulta);
    }

    @PutMapping("/{id}")
    public Consulta update(
            @PathVariable Long id,
            @RequestBody Consulta consulta
    ) {
        return service.update(id, consulta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}