package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.response.ScoreSaudeResponse;
import fiap.com.br.petcarehub.entity.*;
import fiap.com.br.petcarehub.enums.CategoriaScore;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import fiap.com.br.petcarehub.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreSaudeService {

    private final ScoreSaudeRepository repository;
    private final PetService petService;
    private final LeituraColeiraRepository leituraColeiraRepository;
    private final LeituraComedouroRepository leituraComedouroRepository;
    private final LeituraAmbienteRepository leituraAmbienteRepository;
    private final ConsultaRepository consultaRepository;
    private final AlertaSaudeRepository alertaSaudeRepository;
    private final AlertaSaudeService alertaSaudeService;

    @Cacheable(value = "scores", key = "#petId")
    @Transactional
    public ScoreSaudeResponse scoreAtual(Long petId) {
        petService.findEntityById(petId);
        return repository.findTopByPetIdOrderByDataCalculoDesc(petId)
                .map(DtoMapper::toResponse)
                .orElseGet(() -> calcular(petId));
    }

    @CacheEvict(value = "scores", key = "#petId")
    @Transactional
    public ScoreSaudeResponse calcular(Long petId) {
        Pet pet = petService.findEntityById(petId);
        int score = 100;
        StringBuilder observacao = new StringBuilder("Score calculado com base em leituras IoT, consultas e alertas. ");

        Optional<LeituraColeira> coleira = leituraColeiraRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (coleira.isPresent() && coleira.get().getNivelBateria() < 20) {
            score -= 20;
            observacao.append("Bateria da coleira abaixo de 20%. ");
        }

        Optional<LeituraComedouro> comedouro = leituraComedouroRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (comedouro.isPresent()) {
            if (comedouro.get().getNivelRacaoPct() < 20) {
                score -= 10;
                observacao.append("Nível de ração baixo. ");
            }
            if (comedouro.get().getPesoConsumidoG().compareTo(new BigDecimal("30")) < 0) {
                score -= 20;
                observacao.append("Consumo alimentar abaixo do esperado. ");
            }
        }

        Optional<LeituraAmbiente> ambiente = leituraAmbienteRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (ambiente.isPresent()) {
            BigDecimal temp = ambiente.get().getTemperaturaAmbiente();
            if (temp.compareTo(new BigDecimal("10")) < 0 || temp.compareTo(new BigDecimal("32")) > 0) {
                score -= 15;
                observacao.append("Temperatura ambiente fora da faixa segura. ");
            }
            if (ambiente.get().getUmidadePct() < 30 || ambiente.get().getUmidadePct() > 75) {
                score -= 10;
                observacao.append("Umidade ambiente fora da faixa ideal. ");
            }
            if (ambiente.get().getQualidadeArPpm() > 1000) {
                score -= 15;
                observacao.append("Qualidade do ar ruim. ");
            }
        }

        consultaRepository.findTop10ByPetIdOrderByDataConsultaDesc(petId).stream().findFirst().ifPresentOrElse(consulta -> {
            long dias = Math.abs(Duration.between(consulta.getDataConsulta(), LocalDateTime.now()).toDays());
            if (dias > 180) {
                observacao.append("Última consulta há mais de 180 dias. ");
            }
        }, () -> observacao.append("Pet ainda sem consulta registrada. "));

        long alertasGraves = alertaSaudeRepository.countByPetIdAndResolvidoFalseAndNivelIn(petId, java.util.List.of(NivelAlerta.ALTO, NivelAlerta.CRITICO));
        if (alertasGraves > 0) {
            score -= 10;
            observacao.append("Existem alertas graves ativos. ");
        }

        score = Math.max(0, Math.min(100, score));
        CategoriaScore categoria = categoria(score);

        ScoreSaude scoreSaude = ScoreSaude.builder()
                .pet(pet)
                .score(score)
                .categoria(categoria)
                .observacao(observacao.toString().trim())
                .build();

        ScoreSaude salvo = repository.save(scoreSaude);
        petService.atualizarScoreAtual(petId, score);

        if (score < 50) {
            alertaSaudeService.criarInterno(
                    petId,
                    TipoAlerta.SCORE_CRITICO,
                    NivelAlerta.CRITICO,
                    "Score de saúde em nível vermelho. Recomenda-se contato preventivo com a clínica."
            );
        }

        return DtoMapper.toResponse(salvo);
    }

    private CategoriaScore categoria(int score) {
        if (score >= 80) return CategoriaScore.VERDE;
        if (score >= 50) return CategoriaScore.AMARELO;
        return CategoriaScore.VERMELHO;
    }

    @Transactional(readOnly = true)
    public java.util.List<ScoreSaudeResponse> historico(Long petId) {
        petService.findEntityById(petId);
        return repository.findTop10ByPetIdOrderByDataCalculoDesc(petId).stream().map(DtoMapper::toResponse).toList();
    }
}
