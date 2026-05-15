package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.ClinicaResumoDTO;
import fiap.com.br.petcarehub.dto.PetResponseDTO;
import fiap.com.br.petcarehub.dto.ResponsavelResumoDTO;
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
    public List<PetResponseDTO> findAll() {
        return service.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public PetResponseDTO findById(@PathVariable Long id) {
        return toDTO(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponseDTO add(@RequestBody Pet pet) {
        return toDTO(service.add(pet));
    }

    @PutMapping("/{id}")
    public PetResponseDTO update(
            @PathVariable Long id,
            @RequestBody Pet pet
    ) {
        return toDTO(service.update(id, pet));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private PetResponseDTO toDTO(Pet pet) {

        return new PetResponseDTO(

                pet.getId(),
                pet.getNome(),
                pet.getEspecie().name(),
                pet.getPesoKg(),

                new ResponsavelResumoDTO(
                        pet.getResponsavel().getId(),
                        pet.getResponsavel().getNome()
                ),

                new ClinicaResumoDTO(
                        pet.getClinica().getId(),
                        pet.getClinica().getNome()
                )
        );
    }
}