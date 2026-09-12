package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.auth.CurrentUser;
import fiap.com.br.petcarehub.dto.request.PetRequest;
import fiap.com.br.petcarehub.dto.response.PetResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.entity.Tutor;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.repository.PetRepository;
import fiap.com.br.petcarehub.repository.ScoreSaudeRepository;
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
    private final TutorService tutorService;
    private final ClinicaService clinicaService;
    private final ScoreSaudeRepository scoreSaudeRepository;

    @Transactional(readOnly = true)
    public Page<PetResponse> listar(Pageable pageable) {
        if (CurrentUser.isTutor()) {
            return repository.findByTutorId(CurrentUser.tutorId(), pageable).map(DtoMapper::toResponse);
        }
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
        // O dono do pet e sempre o tutor autenticado - nunca o tutorId que vier
        // no corpo da requisicao, senao um tutor consegue cadastrar pet em nome
        // de outro so trocando esse campo.
        Tutor tutor = tutorService.findEntityById(CurrentUser.tutorId());
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
                .tutor(tutor)
                .clinica(clinica)
                .build();

        return DtoMapper.toResponse(repository.save(pet));
    }

    @CacheEvict(value = {"pets", "scores"}, key = "#id")
    @Transactional
    public PetResponse atualizar(Long id, PetRequest request) {
        Pet pet = findEntityById(id);
        verificarPermissao(pet);
        Clinica clinica = clinicaService.findEntityById(request.clinicaId());

        pet.setNome(request.nome());
        pet.setEspecie(request.especie());
        pet.setRaca(request.raca());
        pet.setDataNascimento(request.dataNascimento());
        pet.setPesoKg(request.pesoKg());
        pet.setSexo(request.sexo());
        pet.setCondicoesCronicas(request.condicoesCronicas());
        pet.setAtivo(request.ativo() != null ? request.ativo() : pet.getAtivo()); // ← bug corrigido
        // o dono (tutor) do pet nao muda por aqui - so pela propria checagem de
        // permissao acima ja garante que so o dono atual esta editando
        pet.setClinica(clinica);

        return DtoMapper.toResponse(repository.save(pet));
    }

    @CacheEvict(value = {"pets", "scores"}, key = "#id")
    @Transactional
    public void deletar(Long id) {
        Pet pet = findEntityById(id);
        verificarPermissao(pet);
        repository.delete(pet);
    }

    private void verificarPermissao(Pet pet) {
        if (CurrentUser.isTutor() && !pet.getTutor().getId().equals(CurrentUser.tutorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode alterar os próprios pets.");
        }
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
    public Page<PetResponse> petsEmRisco(
            Long clinicaId,
            String nivel,
            Pageable pageable
    ) {
        int limite;

        if ("vermelho".equalsIgnoreCase(nivel)) {
            limite = 49;
        } else if ("amarelo".equalsIgnoreCase(nivel)) {
            limite = 79;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nível de risco inválido. Use vermelho ou amarelo."
            );
        }

        return scoreSaudeRepository
                .findPetsEmRisco(clinicaId, limite, pageable)
                .map(score -> DtoMapper.toResponse(score.getPet()));
    }

}