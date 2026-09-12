package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.auth.CurrentUser;
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
import java.math.BigDecimal;

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
        if (petId != null) {
            petService.findEntityByIdAutorizado(petId);
        }
        // Sem petId informado, um TUTOR só pode ver alertas dos próprios pets -
        // nunca a lista completa de todos os pets de todos os tutores.
        Long tutorId = CurrentUser.isTutor() ? CurrentUser.tutorId() : null;
        var spec = AlertaSaudeSpecification.filtrar(petId, tipo, resolvido, tutorId);
        return repository.findAll(spec, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AlertaSaude findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public AlertaSaude findEntityByIdAutorizado(Long id) {
        AlertaSaude alerta = findEntityById(id);
        if (CurrentUser.isTutor() && !alerta.getPet().getTutor().getId().equals(CurrentUser.tutorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode alterar alertas dos próprios pets.");
        }
        return alerta;
    }

    @Transactional(readOnly = true)
    public List<AlertaSaudeResponse> alertasAtivosDoPet(Long petId) {
        petService.findEntityByIdAutorizado(petId);
        return repository.findByPetIdAndResolvidoFalseOrderByDataAlertaDesc(petId).stream()
                .map(DtoMapper::toResponse).toList();
    }

    @Transactional
    public AlertaSaudeResponse criar(AlertaSaudeRequest request) {
        return DtoMapper.toResponse(criarInterno(
                request.petId(), request.tipo(), request.nivel(), request.mensagem(),
                request.valorDetectado(), request.limiteReferencia()
        ));
    }

    @Transactional
    public AlertaSaude criarInterno(Long petId, TipoAlerta tipo, NivelAlerta nivel, String mensagem,
                                    BigDecimal valorDetectado, BigDecimal limiteReferencia) {
        Pet pet = petService.findEntityByIdAutorizado(petId);
        AlertaSaude alerta = AlertaSaude.builder()
                .pet(pet)
                .tipo(tipo)
                .nivel(nivel)
                .mensagem(mensagem)
                .valorDetectado(valorDetectado)
                .limiteReferencia(limiteReferencia)
                .resolvido(false)
                .dataAlerta(LocalDateTime.now())
                .build();
        return repository.save(alerta);
    }
    @Transactional
    public AlertaSaudeResponse resolver(Long id) {
        AlertaSaude alerta = findEntityByIdAutorizado(id);
        alerta.setResolvido(true);
        alerta.setDataResolucao(LocalDateTime.now());
        return DtoMapper.toResponse(repository.save(alerta));
    }

    @Transactional
    public void deletar(Long id) {
        AlertaSaude alerta = findEntityByIdAutorizado(id);
        repository.delete(alerta);
    }

    @Transactional(readOnly = true)
    public long contarAlertasGravesAtivos(Long petId) {
        return repository.countByPetIdAndResolvidoFalseAndNivelIn(petId, List.of(NivelAlerta.ALTO, NivelAlerta.CRITICO));
    }

    @Transactional(readOnly = true)
    public List<AlertaSaudeResponse> ultimosPorPet(Long petId) {
        petService.findEntityByIdAutorizado(petId);
        return repository.findTop10ByPetIdOrderByDataAlertaDesc(petId).stream()
                .map(DtoMapper::toResponse).toList();
    }
}