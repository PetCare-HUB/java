package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.ConsultaRequest;
import fiap.com.br.petcarehub.dto.response.ConsultaResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.entity.Consulta;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository repository;
    private final PetService petService;
    private final ClinicaService clinicaService;

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Consulta findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscarPorId(Long id) {
        return DtoMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public ConsultaResponse criar(ConsultaRequest request) {
        Pet pet = petService.findEntityById(request.petId());
        Clinica clinica = clinicaService.findEntityById(request.clinicaId());
        Consulta consulta = Consulta.builder()
                .pet(pet)
                .clinica(clinica)
                .dataConsulta(request.dataConsulta())
                .tipo(request.tipo())
                .observacoes(request.observacoes())
                .valor(request.valor())
                .build();
        return DtoMapper.toResponse(repository.save(consulta));
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        Consulta consulta = findEntityById(id);
        Pet pet = petService.findEntityById(request.petId());
        Clinica clinica = clinicaService.findEntityById(request.clinicaId());
        consulta.setPet(pet);
        consulta.setClinica(clinica);
        consulta.setDataConsulta(request.dataConsulta());
        consulta.setTipo(request.tipo());
        consulta.setObservacoes(request.observacoes());
        consulta.setValor(request.valor());
        return DtoMapper.toResponse(repository.save(consulta));
    }

    @Transactional
    public void deletar(Long id) {
        Consulta consulta = findEntityById(id);
        repository.delete(consulta);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> buscarPorPet(Long petId, Pageable pageable) {
        return repository.findByPetId(petId, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> ultimasPorPet(Long petId) {
        return repository.findTop10ByPetIdOrderByDataConsultaDesc(petId).stream().map(DtoMapper::toResponse).toList();
    }
}
