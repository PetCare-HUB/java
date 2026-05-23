package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.ClinicaRequest;
import fiap.com.br.petcarehub.dto.response.ClinicaAgendaResponse;
import fiap.com.br.petcarehub.dto.response.ClinicaMetricasResponse;
import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.dto.response.ConsultaResponse;
import fiap.com.br.petcarehub.dto.response.EventoPreventivoResponse;
import fiap.com.br.petcarehub.dto.response.PetResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.entity.ScoreSaude;
import fiap.com.br.petcarehub.enums.CategoriaScore;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import fiap.com.br.petcarehub.repository.AlertaSaudeRepository;
import fiap.com.br.petcarehub.repository.ConsultaRepository;
import fiap.com.br.petcarehub.repository.EventoPreventivoRepository;
import fiap.com.br.petcarehub.repository.PetRepository;
import fiap.com.br.petcarehub.repository.ScoreSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository repository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final EventoPreventivoRepository eventoPreventivoRepository;
    private final ScoreSaudeRepository scoreSaudeRepository;
    private final AlertaSaudeRepository alertaSaudeRepository;

    private static final Pattern PERIODO_DIAS = Pattern.compile("^(\\d+)d$", Pattern.CASE_INSENSITIVE);
    private static final long PERIODO_PADRAO_DIAS = 90L;

    @Transactional(readOnly = true)
    public Page<ClinicaResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Clinica findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public ClinicaResponse buscarPorId(Long id) {
        return DtoMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public ClinicaResponse criar(ClinicaRequest request) {
        Clinica clinica = Clinica.builder()
                .nome(request.nome())
                .cnpj(request.cnpj())
                .endereco(request.endereco())
                .telefone(request.telefone())
                .build();
        return DtoMapper.toResponse(repository.save(clinica));
    }

    @Transactional
    public ClinicaResponse atualizar(Long id, ClinicaRequest request) {
        Clinica clinica = findEntityById(id);
        clinica.setNome(request.nome());
        clinica.setCnpj(request.cnpj());
        clinica.setEndereco(request.endereco());
        clinica.setTelefone(request.telefone());
        return DtoMapper.toResponse(repository.save(clinica));
    }

    @Transactional
    public void deletar(Long id) {
        Clinica clinica = findEntityById(id);
        repository.delete(clinica);
    }

    @Transactional(readOnly = true)
    public Page<ClinicaResponse> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PetResponse> petsEmRisco(Long clinicaId, String nivel, Pageable pageable) {
        int limite = (nivel == null || nivel.isBlank() || "vermelho".equalsIgnoreCase(nivel)) ? 49 : 79;
        return petRepository.findPetsEmRisco(clinicaId, limite, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClinicaMetricasResponse metricas(Long clinicaId, String periodo) {
        Clinica clinica = findEntityById(clinicaId);
        long dias = extrairDias(periodo);
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);
        List<ScoreSaude> scores = scoreSaudeRepository.findByPetClinicaIdAndDataCalculoBetween(clinicaId, inicio, fim);
        BigDecimal scoreMedio = scores.isEmpty()
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(scores.stream().mapToInt(ScoreSaude::getScore).average().orElse(0D)).setScale(2, RoundingMode.HALF_UP);

        LocalDate inicioData = inicio.toLocalDate();
        LocalDate fimData = fim.toLocalDate();

        return new ClinicaMetricasResponse(
                clinica.getId(),
                clinica.getNome(),
                normalizarPeriodo(periodo, dias),
                inicio,
                fim,
                petRepository.countByClinicaId(clinicaId),
                petRepository.countByClinicaIdAndAtivoTrue(clinicaId),
                petRepository.countByClinicaIdAndScoreAtualLessThanEqual(clinicaId, 79),
                consultaRepository.countByClinicaIdAndDataConsultaBetween(clinicaId, inicio, fim),
                eventoPreventivoRepository.countByPetClinicaIdAndDataPrevistaBetween(clinicaId, inicioData, fimData),
                eventoPreventivoRepository.countByPetClinicaIdAndDataPrevistaBetweenAndRealizadoFalse(clinicaId, inicioData, fimData),
                alertaSaudeRepository.countByPetClinicaIdAndResolvidoFalse(clinicaId),
                scoreSaudeRepository.countByPetClinicaIdAndDataCalculoBetween(clinicaId, inicio, fim),
                scoreMedio,
                scoreSaudeRepository.countByPetClinicaIdAndDataCalculoBetweenAndCategoria(clinicaId, inicio, fim, CategoriaScore.VERMELHO),
                scoreSaudeRepository.countByPetClinicaIdAndDataCalculoBetweenAndCategoria(clinicaId, inicio, fim, CategoriaScore.AMARELO),
                scoreSaudeRepository.countByPetClinicaIdAndDataCalculoBetweenAndCategoria(clinicaId, inicio, fim, CategoriaScore.VERDE)
        );
    }

    @Transactional(readOnly = true)
    public ClinicaAgendaResponse agendaProximos30Dias(Long clinicaId) {
        Clinica clinica = findEntityById(clinicaId);
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusDays(30);
        LocalDate inicioData = inicio.toLocalDate();
        LocalDate fimData = fim.toLocalDate();

        List<ConsultaResponse> consultas = consultaRepository
                .findByClinicaIdAndDataConsultaBetweenOrderByDataConsultaAsc(clinicaId, inicio, fim)
                .stream()
                .map(DtoMapper::toResponse)
                .toList();

        List<EventoPreventivoResponse> eventosPreventivos = eventoPreventivoRepository
                .findByPetClinicaIdAndDataPrevistaBetweenOrderByDataPrevistaAsc(clinicaId, inicioData, fimData)
                .stream()
                .map(DtoMapper::toResponse)
                .toList();

        return new ClinicaAgendaResponse(
                clinica.getId(),
                clinica.getNome(),
                inicioData,
                fimData,
                consultas,
                eventosPreventivos
        );
    }

    private long extrairDias(String periodo) {
        if (periodo == null || periodo.isBlank()) {
            return PERIODO_PADRAO_DIAS;
        }

        Matcher matcher = PERIODO_DIAS.matcher(periodo.trim());
        if (!matcher.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período inválido. Use o formato Xd, por exemplo 90d.");
        }

        return Long.parseLong(matcher.group(1));
    }

    private String normalizarPeriodo(String periodo, long dias) {
        return (periodo == null || periodo.isBlank()) ? dias + "d" : periodo.trim().toLowerCase();
    }
}
