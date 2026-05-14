package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.service.ClinicaService;
import org.springframework.http.HttpStatus;
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
    public List<Clinica> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Clinica findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Clinica add(@RequestBody Clinica clinica) {
        return service.add(clinica);
    }

    @PutMapping("/{id}")
    public Clinica update(
            @PathVariable Long id,
            @RequestBody Clinica clinica
    ) {
        return service.update(id, clinica);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}