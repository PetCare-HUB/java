package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.PetRequest;
import fiap.com.br.petcarehub.dto.response.PetResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.repository.PetRepository;
import fiap.com.br.petcarehub.specification.PetSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;
    private final ResponsavelService responsavelService;
    private final ClinicaService clinicaService;

    @Transactional(readOnly = true)
    public Page<PetResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    @Cacheable(value = "pets", key = "#id")
    @Transactional(readOnly = true)
    public Pet findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public PetResponse buscarPorId(Long id) {
        return DtoMapper.toResponse(findEntityById(id));
    }

    @CacheEvict(value = {"pets", "scores"}, allEntries = true)
    @Transactional
    public PetResponse criar(PetRequest request) {
        Responsavel responsavel = responsavelService.findEntityById(request.responsavelId());
        Clinica clinica = clinicaService.findEntityById(request.clinicaId());

        Pet pet = Pet.builder()
                .nome(request.nome())
                .especie(request.especie())
                .raca(request.raca())
                .dataNascimento(request.dataNascimento())
                .pesoKg(request.pesoKg())
                .sexo(request.sexo())
                .condicoesCronicas(request.condicoesCronicas())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .scoreAtual(100)
                .responsavel(responsavel)
                .clinica(clinica)
                .build();

        return DtoMapper.toResponse(repository.save(pet));
    }

    @CacheEvict(value = {"pets", "scores"}, key = "#id")
    @Transactional
    public PetResponse atualizar(Long id, PetRequest request) {
        Pet pet = findEntityById(id);
        Responsavel responsavel = responsavelService.findEntityById(request.responsavelId());
        Clinica clinica = clinicaService.findEntityById(request.clinicaId());

        pet.setNome(request.nome());
        pet.setEspecie(request.especie());
        pet.setRaca(request.raca());
        pet.setDataNascimento(request.dataNascimento());
        pet.setPesoKg(request.pesoKg());
        pet.setSexo(request.sexo());
        pet.setCondicoesCronicas(request.condicoesCronicas());
        pet.setAtivo(request.ativo() != null ? request.ativo() : pet.getAtivo()); // ← bug corrigido
        pet.setResponsavel(responsavel);
        pet.setClinica(clinica);

        return DtoMapper.toResponse(repository.save(pet));
    }

    @CacheEvict(value = {"pets", "scores"}, key = "#id")
    @Transactional
    public void deletar(Long id) {
        Pet pet = findEntityById(id);
        repository.delete(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponse> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PetResponse> buscarComFiltros(
            EspeciePet especie,
            String raca,
            Long clinicaId,
            Integer scoreMin,
            Integer scoreMax,
            Pageable pageable
    ) {
        var spec = PetSpecification.filtrar(especie, raca, clinicaId, scoreMin, scoreMax);
        return repository.findAll(spec, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PetResponse> petsEmRisco(Long clinicaId, String nivel, Pageable pageable) {
        int limite = "vermelho".equalsIgnoreCase(nivel) ? 49 : 79;
        return repository.findPetsEmRisco(clinicaId, limite, pageable).map(DtoMapper::toResponse);
    }

    @CacheEvict(value = {"pets", "scores"}, key = "#petId")
    @Transactional
    public void atualizarScoreAtual(Long petId, Integer score) {
        Pet pet = findEntityById(petId);
        pet.setScoreAtual(score);
        repository.save(pet);
    }
}