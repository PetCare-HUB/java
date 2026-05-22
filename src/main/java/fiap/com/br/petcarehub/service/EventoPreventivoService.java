package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.EventoPreventivoRequest;
import fiap.com.br.petcarehub.dto.response.EventoPreventivoResponse;
import fiap.com.br.petcarehub.entity.EventoPreventivo;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.repository.EventoPreventivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoPreventivoService {

    private final EventoPreventivoRepository repository;
    private final PetService petService;

    @Transactional(readOnly = true)
    public List<EventoPreventivoResponse> planoDoPet(Long petId) {
        petService.findEntityById(petId);
        return repository.findByPetIdOrderByDataPrevistaAsc(petId).stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EventoPreventivo findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento preventivo não encontrado: " + id));
    }

    @Transactional
    public EventoPreventivoResponse criar(EventoPreventivoRequest request) {
        Pet pet = petService.findEntityById(request.petId());
        EventoPreventivo evento = EventoPreventivo.builder()
                .pet(pet)
                .tipo(request.tipo())
                .descricao(request.descricao())
                .dataPrevista(request.dataPrevista())
                .realizado(false)
                .build();
        return DtoMapper.toResponse(repository.save(evento));
    }

    @Transactional
    public EventoPreventivoResponse marcarComoRealizado(Long id) {
        EventoPreventivo evento = findEntityById(id);
        evento.setRealizado(true);
        return DtoMapper.toResponse(repository.save(evento));
    }
}
