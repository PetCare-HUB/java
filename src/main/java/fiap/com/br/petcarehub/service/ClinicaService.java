package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.ClinicaRequest;
import fiap.com.br.petcarehub.dto.response.AlertaSaudeResponse;
import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.dto.response.PetResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.repository.AlertaSaudeRepository;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import fiap.com.br.petcarehub.repository.ConsultaRepository;
import fiap.com.br.petcarehub.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClinicaService {

    private final ClinicaRepository repository;
    private final PetRepository petRepository;
    private final AlertaSaudeRepository alertaSaudeRepository;
    private final ConsultaRepository consultaRepository;

    public Page<ClinicaResponse> listar(Pageable pageable) {
        log.debug("Listando clínicas com paginação: {}", pageable);
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    public ClinicaResponse buscarPorId(Long id) {
        log.debug("Buscando clínica ID: {}", id);
        Clinica clinica = findEntityById(id);
        return DtoMapper.toResponse(clinica);
    }

    public Page<ClinicaResponse> buscarPorNome(String nome, Pageable pageable) {
        log.debug("Buscando clínicas por nome: {}", nome);
        return repository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(DtoMapper::toResponse);
    }
    @Transactional
    public ClinicaResponse criar(ClinicaRequest request) {
        log.info("Criando nova clínica: nome={}", request.nome());
        try {
            Clinica clinica = DtoMapper.toClinica(request);
            Clinica salva = repository.save(clinica);
            log.info("Clínica criada com sucesso. ID: {}", salva.getId());
            return DtoMapper.toResponse(salva);
        } catch (Exception e) {
            log.error("Erro ao criar clínica: {}", request.nome(), e);
            throw e;
        }
    }
    @Transactional
    public ClinicaResponse atualizar(Long id, ClinicaRequest request) {
        log.info("Atualizando clínica ID: {}", id);
        try {
            Clinica clinica = findEntityById(id);
            DtoMapper.updateClinica(clinica, request);
            Clinica atualizada = repository.save(clinica);
            log.info("Clínica {} atualizada com sucesso", id);
            return DtoMapper.toResponse(atualizada);
        } catch (Exception e) {
            log.error("Erro ao atualizar clínica {}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando clínica ID: {}", id);
        try {
            findEntityById(id);
            repository.deleteById(id);
            log.info("Clínica {} deletada com sucesso", id);
        } catch (Exception e) {
            log.error("Erro ao deletar clínica {}", id, e);
            throw e;
        }
    }

    public List<PetResponse> petEmRisco(Long clinicaId, String nivel) {
        log.info("Buscando pets em risco da clínica {} - Nível: {}", clinicaId, nivel);
        findEntityById(clinicaId);

        if ("verde".equalsIgnoreCase(nivel)) {
            // Score >= 80
            return petRepository.findByClinicaIdAndScoreAtualGreaterThanEqualAndAtivoTrueOrderByScoreAtualAsc(clinicaId, 80)
                    .stream()
                    .map(DtoMapper::toResponse)
                    .toList();
        } else if ("amarelo".equalsIgnoreCase(nivel)) {
            // Score 50-79
            return petRepository.findByClinicaIdAndScoreAtualBetweenAndAtivoTrueOrderByScoreAtualAsc(clinicaId, 50, 79)
                    .stream()
                    .map(DtoMapper::toResponse)
                    .toList();
        } else {
            // vermelho: Score < 50
            return petRepository.findByClinicaIdAndScoreAtualLessThanAndAtivoTrueOrderByScoreAtualAsc(clinicaId, 50)
                    .stream()
                    .map(DtoMapper::toResponse)
                    .toList();
        }
    }

    public Map<String, Object> metricas(Long clinicaId, String periodo) {
        log.info("Calculando métricas da clínica ID: {} - Período: {}", clinicaId, periodo);
        try {
            findEntityById(clinicaId);

            int dias = 90;
            if (periodo != null && periodo.endsWith("d")) {
                dias = Integer.parseInt(periodo.replace("d", ""));
            }

            LocalDateTime dataInicio = LocalDateTime.now().minusDays(dias);
            LocalDateTime dataFim = LocalDateTime.now();

            long totalPets = petRepository.countByClinicaId(clinicaId);
            long petsAtivos = petRepository.countByClinicaIdAndAtivoTrue(clinicaId);
            long petsEmRisco = petRepository.countByClinicaIdAndScoreAtualLessThan(clinicaId, 50);

            long totalConsultasNoPeriodo = consultaRepository.countByClinicaIdAndDataConsultaBetween(
                    clinicaId, dataInicio, dataFim);

            long totalAlertasNoPeriodo = alertaSaudeRepository.countByPetClinicaIdAndDataAlertaBetween(
                    clinicaId, dataInicio, dataFim);

            Map<String, Object> metricas = new HashMap<>();
            metricas.put("clinicaId", clinicaId);
            metricas.put("totalPets", totalPets);
            metricas.put("petsAtivos", petsAtivos);
            metricas.put("petsEmRisco", petsEmRisco);
            metricas.put("totalConsultasNoPeriodo", totalConsultasNoPeriodo);
            metricas.put("totalAlertasNoPeriodo", totalAlertasNoPeriodo);
            metricas.put("periodoInicio", dataInicio.toString());
            metricas.put("periodoFim", dataFim.toString());

            log.info("Métricas calculadas para clínica {}: {} pets, {} em risco", clinicaId, totalPets, petsEmRisco);

            return metricas;
        } catch (Exception e) {
            log.error("Erro ao calcular métricas da clínica {}", clinicaId, e);
            throw e;
        }
    }

    public List<?> agendaProximos30Dias(Long clinicaId) {
        log.info("Buscando agenda dos próximos 30 dias da clínica {}", clinicaId);
        try {
            findEntityById(clinicaId);

            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime daqui30Dias = agora.plusDays(30);

            var consultas = consultaRepository.findByClinicaIdAndDataConsultaBetweenOrderByDataConsultaAsc(
                    clinicaId, agora, daqui30Dias
            );

            log.info("Encontradas {} consultas nos próximos 30 dias para clínica {}", consultas.size(), clinicaId);
            return consultas.stream().map(DtoMapper::toResponse).toList();
        } catch (Exception e) {
            log.error("Erro ao buscar agenda da clínica {}", clinicaId, e);
            throw e;
        }
    }

    public List<AlertaSaudeResponse> alertasIotHoje(Long clinicaId) {
        log.info("Buscando alertas IoT de hoje da clínica {}", clinicaId);
        try {
            findEntityById(clinicaId);

            LocalDateTime inicioHoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimHoje = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

            var alertas = alertaSaudeRepository.findByPetClinicaIdAndDataAlertaBetweenOrderByDataAlertaDesc(
                            clinicaId, inicioHoje, fimHoje
                    ).stream()
                    .map(DtoMapper::toResponse)
                    .toList();

            log.info("Encontrados {} alertas para clínica {} em: {}", alertas.size(), clinicaId, inicioHoje.toLocalDate());
            return alertas;
        } catch (Exception e) {
            log.error("Erro ao buscar alertas IoT da clínica {}", clinicaId, e);
            throw e;
        }
    }

    Clinica findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Clínica não encontrada. ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica não encontrada");
                });
    }
}