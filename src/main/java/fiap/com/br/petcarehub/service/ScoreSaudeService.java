package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.response.ScoreSaudeResponse;
import fiap.com.br.petcarehub.entity.*;
import fiap.com.br.petcarehub.enums.CategoriaScore;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.StatusAtividade;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import fiap.com.br.petcarehub.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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

    /** Janela da análise de atividade — a mesma variacao_atividade_7d da especificação. */
    private static final int DIAS_JANELA_ATIVIDADE = 7;

    /** Abaixo disso a amostra é pequena demais para concluir qualquer coisa. */
    private static final int MINIMO_LEITURAS_JANELA = 5;

    /** Todo pet dorme: só acima desta fatia de leituras sedentárias há sinal. */
    private static final int LIMITE_SEDENTARISMO_PCT = 40;

    /** Pesos que a especificação reserva para cada componente do score. */
    private static final int PESO_ATIVIDADE = 25;
    private static final int PESO_CONSULTA = 15;

    /** A partir daqui a continuidade do cuidado começa a pesar na nota. */
    private static final int DIAS_CONSULTA_LIMITE = 180;

    private final ScoreSaudeRepository repository;
    private final PetService petService;
    private final LeituraColeiraRepository leituraColeiraRepository;
    private final LeituraComedouroRepository leituraComedouroRepository;
    private final LeituraAmbienteRepository leituraAmbienteRepository;
    private final ConsultaRepository consultaRepository;
    private final AlertaSaudeRepository alertaSaudeRepository;
    private final AlertaSaudeService alertaSaudeService;
    private final PlSqlRepository plSqlRepository;

    // Sem @Cacheable de propósito: o cache era só por petId, e um Cacheable
    // nesse próprio método faria a checagem de dono (dentro do método) ser
    // pulada inteira num cache hit - um tutor B reaproveitaria o resultado
    // cacheado por um acesso legítimo do tutor A e passaria pela validação.
    @Transactional
    public ScoreSaudeResponse scoreAtual(Long petId) {
        petService.findEntityByIdAutorizado(petId);
        Optional<ScoreSaude> ultimoScore = repository.findTopByPetIdOrderByDataCalculoDesc(petId);
        if (ultimoScore.isEmpty() || existeLeituraMaisRecenteQue(petId, ultimoScore.get().getDataCalculo())) {
            return calcular(petId);
        }
        return DtoMapper.toResponse(ultimoScore.get());
    }

    // O score só era recalculado quando ainda não existia nenhum score salvo
    // para o pet (orElseGet antigo). Isso deixava o GET /score-saude devolvendo
    // sempre o mesmo valor congelado após o primeiro cálculo, mesmo com leituras
    // IoT novas chegando depois. Agora comparamos a data do último score com a
    // leitura mais recente de cada sensor e só recalculamos quando há dado novo.
    private boolean existeLeituraMaisRecenteQue(Long petId, LocalDateTime dataCalculo) {
        return leituraColeiraRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId)
                .map(l -> l.getTimestampLeitura().isAfter(dataCalculo)).orElse(false)
                || leituraComedouroRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId)
                .map(l -> l.getTimestampLeitura().isAfter(dataCalculo)).orElse(false)
                || leituraAmbienteRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId)
                .map(l -> l.getTimestampLeitura().isAfter(dataCalculo)).orElse(false);
    }

    @CacheEvict(value = "scores", key = "#petId")
    @Transactional
    public ScoreSaudeResponse calcular(Long petId) {
        Pet pet = petService.findEntityByIdAutorizado(petId);

        int score = 100;
        int scoreAtividade = 100;
        int scoreAlimentacao = 100;
        int scoreAmbiente = 100;
        int scoreConsulta = 100;
        int scorePreventivo = 100; // sem lógica própria ainda — fica neutro até termos regra de vacina/preventivo

        // A observação é a linha do tempo explicativa do score (opção B: persistida na
        // própria SCORE_SAUDE). Compara com o cálculo anterior e lista só indicadores
        // reais. A IA apenas lê esse texto; nunca o altera.
        Optional<ScoreSaude> anterior = repository.findTopByPetIdOrderByDataCalculoDesc(petId);
        StringBuilder observacao = new StringBuilder(); // indicadores que penalizaram
        String ressalva = ""; // limitação de dado, não penaliza

        // ATIVIDADE FÍSICA — sensor MPU6050 da coleira.
        //
        // Olha a PROPORÇÃO de leituras SEDENTARIO na janela de 7 dias, e não a
        // última leitura. É o diferencial que o produto promete ("dormindo
        // demais = risco") e ele só aparece numa janela: uma leitura isolada de
        // pet parado é só um pet cochilando; uma semana inteira parada é sinal
        // clínico. É também o que a especificação pede como variacao_atividade_7d.
        LocalDateTime inicioJanela = LocalDateTime.now().minusDays(DIAS_JANELA_ATIVIDADE);
        long leiturasNaJanela = leituraColeiraRepository
                .countByPetIdAndTimestampLeituraAfter(petId, inicioJanela);

        if (leiturasNaJanela < MINIMO_LEITURAS_JANELA) {
            ressalva = "Poucas leituras da coleira nos últimos " + DIAS_JANELA_ATIVIDADE
                    + " dias para avaliar a atividade. ";
        } else {
            long sedentarias = leituraColeiraRepository
                    .countByPetIdAndStatusAtividadeAndTimestampLeituraAfter(
                            petId, StatusAtividade.SEDENTARIO, inicioJanela);
            int pctSedentario = (int) Math.round(100.0 * sedentarias / leiturasNaJanela);

            // Até o limite é normal: todo pet dorme boa parte do dia. Acima
            // dele a penalidade cresce proporcional ao excedente, até consumir
            // os 25 pontos que a especificação reserva para atividade.
            if (pctSedentario > LIMITE_SEDENTARISMO_PCT) {
                int excedente = pctSedentario - LIMITE_SEDENTARISMO_PCT;
                int penalidade = Math.min(PESO_ATIVIDADE,
                        excedente * PESO_ATIVIDADE / (100 - LIMITE_SEDENTARISMO_PCT));
                score -= penalidade;
                scoreAtividade = 100 - (penalidade * 100 / PESO_ATIVIDADE);
                observacao.append("Pet sedentário em ").append(pctSedentario)
                        .append("% das leituras dos últimos ").append(DIAS_JANELA_ATIVIDADE)
                        .append(" dias. ");
            }
        }

        // Bateria é saúde do DISPOSITIVO, não do pet. Penaliza o total, porque
        // sem coleira carregada o monitoramento para de acontecer, mas fica
        // fora do scoreAtividade — que o app e o dashboard exibem como
        // "atividade do pet" e não pode significar "carga da coleira".
        Optional<LeituraColeira> coleira = leituraColeiraRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (coleira.isPresent() && coleira.get().getNivelBateria() < 20) {
            score -= 5;
            observacao.append("Bateria da coleira abaixo de 20% — o monitoramento pode ficar incompleto. ");
        }

        Optional<LeituraComedouro> comedouro = leituraComedouroRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (comedouro.isPresent()) {
            if (comedouro.get().getNivelRacaoPct() < 20) {
                score -= 10;
                scoreAlimentacao -= 10;
                observacao.append("Nível de ração baixo. ");
            }
            if (comedouro.get().getPesoConsumidoG().compareTo(new BigDecimal("30")) < 0) {
                score -= 20;
                scoreAlimentacao -= 20;
                observacao.append("Consumo alimentar abaixo do esperado. ");
            }
        }

        Optional<LeituraAmbiente> ambiente = leituraAmbienteRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (ambiente.isPresent()) {
            BigDecimal temp = ambiente.get().getTemperaturaAmbiente();
            if (temp.compareTo(new BigDecimal("10")) < 0 || temp.compareTo(new BigDecimal("32")) > 0) {
                score -= 15;
                scoreAmbiente -= 15;
                observacao.append("Temperatura ambiente fora da faixa segura. ");
            }
            if (ambiente.get().getUmidadePct() < 30 || ambiente.get().getUmidadePct() > 75) {
                score -= 10;
                scoreAmbiente -= 10;
                observacao.append("Umidade ambiente fora da faixa ideal. ");
            }
            if (ambiente.get().getQualidadeArPpm() > 1000) {
                score -= 15;
                scoreAmbiente -= 15;
                observacao.append("Qualidade do ar ruim. ");
            }
        }

        // CONTINUIDADE DO CUIDADO — dias desde a última consulta.
        // Antes isto só gerava texto e não mexia na nota, então o sub-score
        // "consulta" não media consulta nenhuma.
        Optional<Consulta> ultimaConsulta = consultaRepository
                .findTopByPetIdOrderByDataConsultaDesc(petId);

        if (ultimaConsulta.isEmpty()) {
            score -= PESO_CONSULTA;
            scoreConsulta = 0;
            observacao.append("Pet ainda sem consulta registrada. ");
        } else {
            // Sem Math.abs: consulta com data futura é agendamento, não
            // atraso — e mascarar o sinal esconderia dado inconsistente.
            long dias = Duration.between(ultimaConsulta.get().getDataConsulta(), LocalDateTime.now()).toDays();
            if (dias > DIAS_CONSULTA_LIMITE) {
                long excedente = dias - DIAS_CONSULTA_LIMITE;
                int penalidade = (int) Math.min(PESO_CONSULTA,
                        excedente * PESO_CONSULTA / DIAS_CONSULTA_LIMITE);
                score -= penalidade;
                scoreConsulta = 100 - (penalidade * 100 / PESO_CONSULTA);
                observacao.append("Última consulta há ").append(dias).append(" dias. ");
            }
        }

        long alertasGraves = alertaSaudeRepository.countByPetIdAndResolvidoFalseAndNivelIn(petId, java.util.List.of(NivelAlerta.ALTO, NivelAlerta.CRITICO));
        if (alertasGraves > 0) {
            // Alerta grave é consequência dos sensores, não falta de consulta:
            // desconta do total sem contaminar o sub-score de consulta.
            score -= 10;
            observacao.append("Existem ").append(alertasGraves).append(" alerta(s) grave(s) ativo(s). ");
        }

        score = Math.max(0, Math.min(100, score));
        scoreAtividade = Math.max(0, Math.min(100, scoreAtividade));
        scoreAlimentacao = Math.max(0, Math.min(100, scoreAlimentacao));
        scoreAmbiente = Math.max(0, Math.min(100, scoreAmbiente));
        scoreConsulta = Math.max(0, Math.min(100, scoreConsulta));
        scorePreventivo = Math.max(0, Math.min(100, scorePreventivo));

        CategoriaScore categoria = categoria(score);

        ScoreSaude scoreSaude = ScoreSaude.builder()
                .pet(pet)
                .scoreTotal(score)
                .scoreAtividade(scoreAtividade)
                .scoreAlimentacao(scoreAlimentacao)
                .scoreAmbiente(scoreAmbiente)
                .scoreConsulta(scoreConsulta)
                .scorePreventivo(scorePreventivo)
                .categoria(categoria)
                .observacao(limitar((montarObservacao(anterior, score, observacao) + (ressalva.isEmpty() ? "" : " " + ressalva)).trim()))
                .build();

        ScoreSaude salvo = repository.save(scoreSaude);

        if (score < 50) {
            alertaSaudeService.criarInterno(
                    petId,
                    TipoAlerta.SCORE_CRITICO,
                    NivelAlerta.CRITICO,
                    "Score de saúde em nível vermelho. Recomenda-se contato preventivo com a clínica.",
                    new BigDecimal(score),
                    new BigDecimal(50)
            );
        }

        return DtoMapper.toResponse(salvo);
    }

    /**
     * Frase de variação (dados reais do score anterior) + indicadores do cálculo atual.
     * Quando a nota mudou mas nenhum indicador atual a explica, registra a limitação
     * em vez de inventar uma causa.
     */
    private String montarObservacao(Optional<ScoreSaude> anterior, int scoreNovo, StringBuilder indicadores) {
        StringBuilder texto = new StringBuilder();
        int diferenca = 0;
        if (anterior.isEmpty()) {
            texto.append("Primeiro cálculo do score registrado para o pet. ");
        } else {
            int antes = anterior.get().getScoreTotal();
            diferenca = scoreNovo - antes;
            if (diferenca == 0) {
                texto.append("Score permaneceu estável em ").append(scoreNovo).append(" pontos. ");
            } else if (diferenca < 0) {
                texto.append("Score diminuiu de ").append(antes).append(" para ").append(scoreNovo)
                        .append(" (").append(diferenca).append(" pontos). ");
            } else {
                texto.append("Score aumentou de ").append(antes).append(" para ").append(scoreNovo)
                        .append(" (+").append(diferenca).append(" pontos). ");
            }
        }

        texto.append("Indicadores do período: ");
        if (indicadores.length() == 0) {
            texto.append("sem alterações relevantes nos indicadores disponíveis. ");
            if (diferenca != 0) {
                texto.append("A variação não pôde ser atribuída a um indicador específico do cálculo atual. ");
            }
        } else {
            texto.append(indicadores);
        }
        return texto.toString().trim();
    }

    private String limitar(String texto) {
        if (texto.length() <= 1000) {
            return texto;
        }
        return texto.substring(0, 997) + "...";
    }

    private CategoriaScore categoria(int score) {
        if (score >= 80) return CategoriaScore.VERDE;
        if (score >= 50) return CategoriaScore.AMARELO;
        return CategoriaScore.VERMELHO;
    }

    /**
     * Média do score no período, calculada pela function {@code FN_SCORE_MEDIO_PET}
     * do Oracle — a mesma que a disciplina de Database entregou.
     *
     * <p>O cálculo fica no banco de propósito: é agregação sobre o histórico, e
     * trazer todas as linhas para somar em Java seria desperdício de rede para
     * chegar ao mesmo número. Devolve também a idade do pet em meses, de
     * {@code FN_CALCULAR_IDADE_PET}, porque é o contexto que dá sentido à média
     * (um filhote e um pet idoso não se comparam pelo mesmo padrão).</p>
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> mediaPeriodo(Long petId, int dias) {
        petService.findEntityByIdAutorizado(petId);

        java.util.Map<String, Object> resposta = new java.util.LinkedHashMap<>();
        resposta.put("petId", petId);
        resposta.put("periodoDias", dias);
        resposta.put("scoreMedio", plSqlRepository.scoreMedio(petId, dias));
        resposta.put("idadeMeses", plSqlRepository.idadeEmMeses(petId));
        resposta.put("calculadoPor", "FN_SCORE_MEDIO_PET / FN_CALCULAR_IDADE_PET (Oracle PL/SQL)");
        return resposta;
    }

    @Transactional(readOnly = true)
    public java.util.List<ScoreSaudeResponse> historico(Long petId) {
        petService.findEntityByIdAutorizado(petId);
        return repository.findTop10ByPetIdOrderByDataCalculoDesc(petId).stream().map(DtoMapper::toResponse).toList();
    }
}