package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import fiap.com.br.petcarehub.projection.PetSummary;
import fiap.com.br.petcarehub.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PetService {

    private final PetRepository repository;

    @Cacheable(value = "pets", key = "#id")
    public Pet findPetById(Long id) {
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

    public Page<Pet> getAllPaginado(Pageable pageable) {
        return repository.findAll(pageable);
    }
    @CacheEvict(value = "pets", allEntries = true)
    public Pet add(Pet pet) {
        return repository.save(pet);
    }

    public Pet findById(Long id) {
        return findPetById(id);
    }

    @CacheEvict(value = "pets", key = "#id")
    public void delete(Long id) {
        findPetById(id);
        repository.deleteById(id);
    }

    @CacheEvict(value = "pets", key = "#id")
    public Pet update(Long id, Pet newPet) {
        findPetById(id);
        newPet.setId(id);
        return repository.save(newPet);
    }

    public Page<PetSummary> getByNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Page<PetSummary> getByEspecie(EspeciePet especie, Pageable pageable) {
        return repository.findByEspecie(especie, pageable);
    }


    public Page<PetSummary> getByRaca(String raca, Pageable pageable) {
        return repository.findByRacaContaining(raca, pageable);
    }

    public Page<PetSummary> getBySexo(SexoPet sexo, Pageable pageable) {
        return repository.findBySexo(sexo, pageable);
    }
}