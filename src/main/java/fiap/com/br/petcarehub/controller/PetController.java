package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.service.PetService;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{id}")
    public Pet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet add(@RequestBody Pet pet) {
        return service.add(pet);
    }

    @PutMapping("/{id}")
    public Pet update(
            @PathVariable Long id,
            @RequestBody Pet pet
    ) {
        return service.update(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}