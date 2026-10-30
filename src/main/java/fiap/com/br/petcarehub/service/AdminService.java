package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Credenciamento da rede: o admin da PetCare Hub decide quais clinicas
 * que se cadastraram passam a poder autenticar.
 *
 * Fica separado do ClinicaService de proposito - aquele e o CRUD que a
 * propria clinica usa; este e a visao de quem administra a plataforma.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final ClinicaRepository clinicaRepository;

    /** Fila de credenciamento. Sem filtro, lista a rede inteira. */
    public Page<ClinicaResponse> listarClinicas(StatusAcesso status, Pageable pageable) {
        log.debug("Admin listando clínicas. Filtro de status: {}", status);
        Page<Clinica> clinicas = status == null
                ? clinicaRepository.findAll(pageable)
                : clinicaRepository.findByStatusAcesso(status, pageable);
        return clinicas.map(DtoMapper::toResponse);
    }

    @Transactional
    public ClinicaResponse aprovar(Long id) {
        Clinica clinica = findEntityById(id);

        if (clinica.getStatusAcesso() == StatusAcesso.ATIVO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta clínica já está aprovada.");
        }

        // Sem senha a clinica nao logaria nem depois de aprovada. Nenhum
        // caminho de cadastro produz esse estado hoje, entao aqui so chega
        // registro legado ou escrita direta no banco - e aprovar nao e o
        // conserto, porque nao ha senha para aprovar.
        if (clinica.getSenha() == null || clinica.getSenha().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta clínica não tem senha definida e por isso não pode ser aprovada.");
        }

        clinica.setStatusAcesso(StatusAcesso.ATIVO);
        clinica.setAtivo('S');
        log.info("Clínica {} aprovada pelo admin", id);
        return DtoMapper.toResponse(clinicaRepository.save(clinica));
    }

    @Transactional
    public ClinicaResponse bloquear(Long id) {
        Clinica clinica = findEntityById(id);
        clinica.setStatusAcesso(StatusAcesso.BLOQUEADO);
        log.info("Clínica {} bloqueada pelo admin", id);
        return DtoMapper.toResponse(clinicaRepository.save(clinica));
    }

    private Clinica findEntityById(Long id) {
        return clinicaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Clínica não encontrada. ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica não encontrada");
                });
    }
}
