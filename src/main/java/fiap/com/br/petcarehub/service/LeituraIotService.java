package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.*;
import fiap.com.br.petcarehub.dto.response.*;
import fiap.com.br.petcarehub.entity.*;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import fiap.com.br.petcarehub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeituraIotService {

    private final PetService petService;
    private final AlertaSaudeService alertaSaudeService;
    private final ScoreSaudeService scoreSaudeService;
    private final LeituraColeiraRepository leituraColeiraRepository;
    private final LeituraComedouroRepository leituraComedouroRepository;
    private final LeituraAmbienteRepository leituraAmbienteRepository;

    @Transactional
    public LeituraColeiraResponse registrarColeira(LeituraColeiraRequest request) {
        Pet pet = petService.findEntityById(request.petId());
        LeituraColeira leitura = LeituraColeira.builder()
                .pet(pet)
                .statusAtividade(request.statusAtividade())
                .nivelBateria(request.nivelBateria())
                .timestampLeitura(request.timestampLeitura() == null ? LocalDateTime.now() : request.timestampLeitura())
                .build();
        LeituraColeira salva = leituraColeiraRepository.save(leitura);

        if (request.nivelBateria() < 20) {
            alertaSaudeService.criarInterno(
                    request.petId(),
                    TipoAlerta.BATERIA_COLEIRA_BAIXA,
                    NivelAlerta.MEDIO,
                    "Bateria da coleira abaixo de 20%. Recarregue o dispositivo."
            );
        }
        scoreSaudeService.calcular(request.petId());
        return DtoMapper.toResponse(salva);
    }

    @Transactional
    public LeituraComedouroResponse registrarComedouro(LeituraComedouroRequest request) {
        Pet pet = petService.findEntityById(request.petId());
        LeituraComedouro leitura = LeituraComedouro.builder()
                .pet(pet)
                .nivelRacaoPct(request.nivelRacaoPct())
                .pesoConsumidoG(request.pesoConsumidoG())
                .timestampLeitura(request.timestampLeitura() == null ? LocalDateTime.now() : request.timestampLeitura())
                .build();
        LeituraComedouro salva = leituraComedouroRepository.save(leitura);

        if (request.nivelRacaoPct() < 20) {
            alertaSaudeService.criarInterno(
                    request.petId(),
                    TipoAlerta.RACAO_BAIXA,
                    NivelAlerta.MEDIO,
                    "Nível de ração abaixo de 20%. Necessário reabastecer o comedouro."
            );
        }
        if (request.pesoConsumidoG().compareTo(new BigDecimal("30")) < 0) {
            alertaSaudeService.criarInterno(
                    request.petId(),
                    TipoAlerta.BAIXA_ALIMENTACAO,
                    NivelAlerta.ALTO,
                    "Consumo alimentar abaixo do esperado para a última refeição."
            );
        }
        scoreSaudeService.calcular(request.petId());
        return DtoMapper.toResponse(salva);
    }

    @Transactional
    public LeituraAmbienteResponse registrarAmbiente(LeituraAmbienteRequest request) {
        Pet pet = petService.findEntityById(request.petId());
        LeituraAmbiente leitura = LeituraAmbiente.builder()
                .pet(pet)
                .temperaturaAmbiente(request.temperaturaAmbiente())
                .umidadePct(request.umidadePct())
                .qualidadeArPpm(request.qualidadeArPpm())
                .petPresente(request.petPresente())
                .timestampLeitura(request.timestampLeitura() == null ? LocalDateTime.now() : request.timestampLeitura())
                .build();
        LeituraAmbiente salva = leituraAmbienteRepository.save(leitura);

        if (request.qualidadeArPpm() > 1000) {
            alertaSaudeService.criarInterno(
                    request.petId(),
                    TipoAlerta.AMBIENTE_RUIM,
                    NivelAlerta.ALTO,
                    "Qualidade do ar acima de 1000 ppm. Verifique ventilação do ambiente."
            );
        }
        if (request.temperaturaAmbiente().compareTo(new BigDecimal("10")) < 0 || request.temperaturaAmbiente().compareTo(new BigDecimal("32")) > 0) {
            alertaSaudeService.criarInterno(
                    request.petId(),
                    TipoAlerta.TEMPERATURA_FORA_DA_FAIXA,
                    NivelAlerta.MEDIO,
                    "Temperatura ambiente fora da faixa segura."
            );
        }
        if (request.umidadePct() < 30 || request.umidadePct() > 75) {
            alertaSaudeService.criarInterno(
                    request.petId(),
                    TipoAlerta.UMIDADE_FORA_DA_FAIXA,
                    NivelAlerta.MEDIO,
                    "Umidade ambiente fora da faixa ideal."
            );
        }
        scoreSaudeService.calcular(request.petId());
        return DtoMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<LeituraColeiraResponse> buscarColeira(Long petId, LocalDateTime de, LocalDateTime ate) {
        petService.findEntityById(petId);
        return leituraColeiraRepository.findByPetIdAndTimestampLeituraBetweenOrderByTimestampLeituraDesc(petId, ajustarDe(de), ajustarAte(ate))
                .stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraComedouroResponse> buscarComedouro(Long petId, LocalDateTime de, LocalDateTime ate) {
        petService.findEntityById(petId);
        return leituraComedouroRepository.findByPetIdAndTimestampLeituraBetweenOrderByTimestampLeituraDesc(petId, ajustarDe(de), ajustarAte(ate))
                .stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraAmbienteResponse> buscarAmbiente(Long petId, LocalDateTime de, LocalDateTime ate) {
        petService.findEntityById(petId);
        return leituraAmbienteRepository.findByPetIdAndTimestampLeituraBetweenOrderByTimestampLeituraDesc(petId, ajustarDe(de), ajustarAte(ate))
                .stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraColeiraResponse> ultimasColeira(Long petId) {
        return leituraColeiraRepository.findTop10ByPetIdOrderByTimestampLeituraDesc(petId).stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraComedouroResponse> ultimasComedouro(Long petId) {
        return leituraComedouroRepository.findTop10ByPetIdOrderByTimestampLeituraDesc(petId).stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraAmbienteResponse> ultimasAmbiente(Long petId) {
        return leituraAmbienteRepository.findTop10ByPetIdOrderByTimestampLeituraDesc(petId).stream().map(DtoMapper::toResponse).toList();
    }

    private LocalDateTime ajustarDe(LocalDateTime de) {
        return de == null ? LocalDateTime.now().minusDays(30) : de;
    }

    private LocalDateTime ajustarAte(LocalDateTime ate) {
        return ate == null ? LocalDateTime.now().plusMinutes(1) : ate;
    }
}
