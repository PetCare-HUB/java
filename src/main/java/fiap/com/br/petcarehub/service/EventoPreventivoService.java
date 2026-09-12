package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.auth.CurrentUser;
import fiap.com.br.petcarehub.dto.request.EventoPreventivoRequest;
import fiap.com.br.petcarehub.dto.request.EventoPreventivoUpdateRequest;
import fiap.com.br.petcarehub.dto.response.EventoPreventivoResponse;
import fiap.com.br.petcarehub.entity.EventoPreventivo;
import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.StatusEventoPreventivo;
import fiap.com.br.petcarehub.repository.EventoPreventivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoPreventivoService {

    private final EventoPreventivoRepository repository;
    private final PetService petService;

    @Transactional(readOnly = true)
    public List<EventoPreventivoResponse> planoDoPet(Long petId) {
        petService.findEntityById(petId);

        return repository.findByPetIdOrderByDataPrevistaAsc(petId)
                .stream()
                .map(DtoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoPreventivo findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento preventivo não encontrado: " + id
                ));
    }

    @Transactional
    public EventoPreventivoResponse criar(EventoPreventivoRequest request) {

        Pet pet = petService.findEntityById(request.petId());
        if (CurrentUser.isTutor() && !pet.getTutor().getId().equals(CurrentUser.tutorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode criar lembretes para os próprios pets.");
        }

        EventoPreventivo evento = EventoPreventivo.builder()
                .pet(pet)
                .tipo(request.tipo())
                .descricao(request.descricao())
                .dataPrevista(request.dataPrevista())
                .status(StatusEventoPreventivo.PENDENTE)
                .build();

        return DtoMapper.toResponse(repository.save(evento));
    }

    @Transactional
    public EventoPreventivoResponse marcarComoRealizado(Long id) {

        EventoPreventivo evento = findEntityById(id);
        verificarPermissao(evento);

        evento.setStatus(StatusEventoPreventivo.REALIZADO);
        evento.setDataRealizacao(LocalDate.now());

        return DtoMapper.toResponse(repository.save(evento));
    }

    @Transactional
    public EventoPreventivoResponse atualizar(Long id, EventoPreventivoUpdateRequest request) {
        EventoPreventivo evento = findEntityById(id);
        verificarPermissao(evento);
        verificarNaoRealizado(evento);

        evento.setTipo(request.tipo());
        evento.setDescricao(request.descricao());
        evento.setDataPrevista(request.dataPrevista());

        return DtoMapper.toResponse(repository.save(evento));
    }

    @Transactional
    public void deletar(Long id) {
        EventoPreventivo evento = findEntityById(id);
        verificarPermissao(evento);
        verificarNaoRealizado(evento);

        repository.delete(evento);
    }

    private void verificarPermissao(EventoPreventivo evento) {
        if (CurrentUser.isTutor() && !evento.getPet().getTutor().getId().equals(CurrentUser.tutorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode alterar lembretes dos próprios pets.");
        }
    }

    private void verificarNaoRealizado(EventoPreventivo evento) {
        if (evento.getStatus() == StatusEventoPreventivo.REALIZADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Um lembrete já realizado não pode ser editado ou excluído.");
        }
    }
}
