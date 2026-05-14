package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository repository;

    private Pet findPetById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ID " + id + " não encontrado"
                )
        );
    }

    public List<Pet> findAll() {
        return repository.findAll();
    }

    public Pet add(Pet pet) {
        return repository.save(pet);
    }

    public Pet findById(Long id) {
        return findPetById(id);
    }

    public void delete(Long id) {
        findPetById(id);
        repository.deleteById(id);
    }

    public Pet update(Long id, Pet newPet) {
        findPetById(id);
        newPet.setId(id);
        return repository.save(newPet);
    }
}