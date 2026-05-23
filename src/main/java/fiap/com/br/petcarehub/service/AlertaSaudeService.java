package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.AlertaSaudeRequest;
import fiap.com.br.petcarehub.dto.response.AlertaSaudeResponse;
import fiap.com.br.petcarehub.entity.AlertaSaude;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import fiap.com.br.petcarehub.repository.AlertaSaudeRepository;
import fiap.com.br.petcarehub.specification.AlertaSaudeSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaSaudeService {

    private final AlertaSaudeRepository repository;
    private final PetService petService;

    @Transactional(readOnly = true)
    public Page<AlertaSaudeResponse> buscar(Long petId, TipoAlerta tipo, Boolean resolvido, Pageable pageable) {
        var spec = AlertaSaudeSpecification.filtrar(petId, tipo, resolvido);
        return repository.findAll(spec, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AlertaSaude findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<AlertaSaudeResponse> alertasAtivosDoPet(Long petId) {
        petService.findEntityById(petId);
        return repository.findByPetIdAndResolvidoFalseOrderByDataCriacaoDesc(petId).stream()
                .map(DtoMapper::toResponse).toList();
    }

    @Transactional
    public AlertaSaudeResponse criar(AlertaSaudeRequest request) {
        return DtoMapper.toResponse(criarInterno(request.petId(), request.tipo(), request.nivel(), request.mensagem()));
    }

    @Transactional
    public AlertaSaude criarInterno(Long petId, TipoAlerta tipo, NivelAlerta nivel, String mensagem) {
        Pet pet = petService.findEntityById(petId);
        AlertaSaude alerta = AlertaSaude.builder()
                .pet(pet)
                .tipo(tipo)
                .nivel(nivel)
                .mensagem(mensagem)
                .resolvido(false)
                .build();
        return repository.save(alerta);
    }

    @Transactional
    public AlertaSaudeResponse resolver(Long id) {
        AlertaSaude alerta = findEntityById(id);
        alerta.setResolvido(true);
        alerta.setDataResolucao(LocalDateTime.now());
        return DtoMapper.toResponse(repository.save(alerta));
    }

    @Transactional
    public void deletar(Long id) {
        AlertaSaude alerta = findEntityById(id);
        repository.delete(alerta);
    }

    @Transactional(readOnly = true)
    public long contarAlertasGravesAtivos(Long petId) {
        return repository.countByPetIdAndResolvidoFalseAndNivelIn(petId, List.of(NivelAlerta.ALTO, NivelAlerta.CRITICO));
    }

    @Transactional(readOnly = true)
    public List<AlertaSaudeResponse> ultimosPorPet(Long petId) {
        return repository.findTop10ByPetIdOrderByDataCriacaoDesc(petId).stream()
                .map(DtoMapper::toResponse).toList();
    }
}