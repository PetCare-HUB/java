package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.entity.*;
import fiap.com.br.petcarehub.enums.StatusAtividade;
import fiap.com.br.petcarehub.enums.StatusEventoPreventivo;
import fiap.com.br.petcarehub.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Monta o bloco "DADOS DO PET" que vai para o modelo.
 *
 * <p>Regra de ouro: aqui só entra o que está gravado no banco, do jeito que
 * está gravado. Nenhuma regra de negócio é recalculada (o score é o último
 * salvo, os alertas são os que o sistema já abriu) — se a IA recalculasse ou
 * se o contexto fosse "enfeitado", a resposta poderia contradizer o que o
 * tutor vê na tela. Cada seção diz de onde veio e diz "sem registros" quando
 * estiver vazia, para o modelo não inventar o que falta.</p>
 *
 * <p>Dados do tutor (nome, CPF, e-mail) ficam de fora de propósito: não ajudam
 * a interpretar a saúde do pet e não precisam sair para um provedor externo.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContextoSaudePetService {

    /** Mesma janela do ScoreSaudeService (variacao_atividade_7d). */
    private static final int DIAS_JANELA_ATIVIDADE = 7;

    /** Mesmo limite do ScoreSaudeService para considerar a amostra pequena. */
    private static final int MINIMO_LEITURAS_JANELA = 5;

    private static final int MAX_CONSULTAS = 3;
    private static final int MAX_ALERTAS_RESOLVIDOS = 5;

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String SEM_REGISTROS = "sem registros";

    private final PetService petService;
    private final PetRepository petRepository;
    private final LeituraColeiraRepository leituraColeiraRepository;
    private final LeituraComedouroRepository leituraComedouroRepository;
    private final LeituraAmbienteRepository leituraAmbienteRepository;
    private final AlertaSaudeRepository alertaSaudeRepository;
    private final ConsultaRepository consultaRepository;
    private final ScoreSaudeRepository scoreSaudeRepository;
    private final EventoPreventivoRepository eventoPreventivoRepository;

    @Transactional(readOnly = true)
    public String montarContexto(Long petId) {
        // Checagem de dono: nunca pular. Lança 403/404 antes de ler qualquer dado.
        Pet autorizado = petService.findEntityByIdAutorizado(petId);

        // findEntityById é @Cacheable: o Pet que volta pode estar destacado e a
        // clínica (LAZY) estouraria LazyInitializationException. Relemos a
        // entidade gerenciada nesta transação só para navegar nas relações.
        Pet pet = petRepository.findById(petId).orElse(autorizado);

        LocalDateTime agora = LocalDateTime.now();
        StringBuilder sb = new StringBuilder();

        // Sem esta linha o modelo so ve carimbos absolutos e supoe que a leitura
        // mais recente e de hoje -- chegou a descrever alertas de tres dias atras
        // como "registrados hoje". Dizer que dia e hoje deixa ele calcular ha
        // quanto tempo cada registro aconteceu.
        sb.append("Data e hora de agora: ").append(agora.format(FORMATO_DATA_HORA))
                .append(". Use esta referencia para dizer ha quanto tempo cada registro ")
                .append("aconteceu; nao presuma que a leitura mais recente e de hoje.\n\n");

        secaoPet(sb, pet);
        secaoColeira(sb, petId, agora);
        secaoComedouro(sb, petId);
        secaoAmbiente(sb, petId);
        secaoAlertas(sb, petId);
        secaoConsultas(sb, petId, agora);
        secaoScore(sb, petId);
        secaoPreventivo(sb, petId);

        sb.append("\nFim dos dados do pet. Qualquer informação não listada acima NÃO está disponível.\n");
        return sb.toString();
    }

    /**
     * Frase curta com o que está fora do normal no pet, usada como consulta da
     * busca na base de conhecimento (RAG) quando não há uma pergunta do tutor.
     * O QuestionAnswerAdvisor busca pela última mensagem do usuário; se ela fosse
     * o bloco inteiro de dados, o embedding ficaria diluído e os trechos
     * recuperados seriam os mesmos para qualquer pet.
     */
    @Transactional(readOnly = true)
    public String resumoParaBusca(Long petId) {
        petService.findEntityByIdAutorizado(petId);
        List<String> partes = new ArrayList<>();

        scoreSaudeRepository.findTopByPetIdOrderByDataCalculoDesc(petId).ifPresent(s -> {
            partes.add("score de saúde " + s.getCategoria() + " " + s.getScoreTotal());
            if (s.getScoreAtividade() < 100) partes.add("atividade física reduzida sedentarismo");
            if (s.getScoreAlimentacao() < 100) partes.add("alimentação consumo de ração baixo");
            if (s.getScoreAmbiente() < 100) partes.add("ambiente temperatura umidade qualidade do ar");
            if (s.getScoreConsulta() < 100) partes.add("consulta veterinária atrasada continuidade do cuidado");
        });
        alertaSaudeRepository.findByPetIdAndResolvidoFalseOrderByDataAlertaDesc(petId)
                .forEach(a -> partes.add("alerta " + a.getTipo().name().toLowerCase().replace('_', ' ')));

        return partes.isEmpty()
                ? "interpretação geral da saúde do pet, score de saúde e quando procurar o veterinário"
                : "interpretação da saúde do pet: " + String.join(", ", partes);
    }

    /** Descreve um alerta específico, para a explicação de alerta. */
    @Transactional(readOnly = true)
    public String montarContextoAlerta(AlertaSaude alerta) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALERTA EM QUESTÃO (fonte: tabela ALERTA_SAUDE, gerado pelas regras do PetCare Hub)\n");
        sb.append("- Id: ").append(alerta.getId()).append('\n');
        sb.append("- Tipo: ").append(alerta.getTipo()).append('\n');
        sb.append("- Nível: ").append(alerta.getNivel()).append('\n');
        sb.append("- Mensagem: ").append(texto(alerta.getMensagem())).append('\n');
        sb.append("- Valor detectado: ").append(numero(alerta.getValorDetectado()))
                .append(" | Limite de referência: ").append(numero(alerta.getLimiteReferencia())).append('\n');
        sb.append("- Data do alerta: ").append(dataHora(alerta.getDataAlerta())).append('\n');
        sb.append("- Resolvido: ").append(Boolean.TRUE.equals(alerta.getResolvido()) ? "sim" : "não");
        if (alerta.getDataResolucao() != null) {
            sb.append(" (em ").append(dataHora(alerta.getDataResolucao())).append(')');
        }
        sb.append('\n');
        return sb.toString();
    }

    // ---------------------------------------------------------------- seções

    private void secaoPet(StringBuilder sb, Pet pet) {
        sb.append("PET (fonte: cadastro do pet)\n");
        sb.append("- Nome: ").append(texto(pet.getNome())).append('\n');
        sb.append("- Espécie: ").append(pet.getEspecie()).append('\n');
        sb.append("- Raça: ").append(textoOu(pet.getRaca(), "não informada")).append('\n');
        sb.append("- Sexo: ").append(pet.getSexo() == null ? "não informado" : pet.getSexo()).append('\n');
        sb.append("- Idade: ").append(idade(pet.getDataNascimento())).append('\n');
        sb.append("- Peso: ").append(pet.getPesoKg() == null ? "não informado" : numero(pet.getPesoKg()) + " kg").append('\n');
        sb.append("- Condições crônicas: ").append(textoOu(pet.getCondicoesCronicas(), "nenhuma registrada")).append('\n');
        sb.append("- Cadastro ativo: ").append(Boolean.FALSE.equals(pet.getAtivo()) ? "não" : "sim").append('\n');
        if (pet.getClinica() != null) {
            sb.append("- Clínica responsável: ").append(texto(pet.getClinica().getNome())).append('\n');
        }
    }

    private void secaoColeira(StringBuilder sb, Long petId, LocalDateTime agora) {
        sb.append("\nCOLEIRA — ATIVIDADE (fonte: leituras IoT da coleira, últimos ")
                .append(DIAS_JANELA_ATIVIDADE).append(" dias)\n");
        LocalDateTime inicio = agora.minusDays(DIAS_JANELA_ATIVIDADE);
        long total = leituraColeiraRepository.countByPetIdAndTimestampLeituraAfter(petId, inicio);

        if (total == 0) {
            sb.append("- Leituras na janela: ").append(SEM_REGISTROS).append('\n');
        } else {
            sb.append("- Leituras na janela: ").append(total).append('\n');
            sb.append("- Distribuição: ");
            StatusAtividade[] status = StatusAtividade.values();
            for (int i = 0; i < status.length; i++) {
                long qtd = leituraColeiraRepository
                        .countByPetIdAndStatusAtividadeAndTimestampLeituraAfter(petId, status[i], inicio);
                sb.append(status[i]).append(' ').append(Math.round(100.0 * qtd / total)).append('%');
                sb.append(i < status.length - 1 ? ", " : "\n");
            }
            if (total < MINIMO_LEITURAS_JANELA) {
                sb.append("- Observação: amostra pequena (menos de ").append(MINIMO_LEITURAS_JANELA)
                        .append(" leituras na janela); o sistema não tira conclusão de atividade com tão poucos dados.\n");
            }
        }

        Optional<LeituraColeira> ultima = leituraColeiraRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (ultima.isPresent()) {
            LeituraColeira l = ultima.get();
            sb.append("- Última leitura: status ").append(l.getStatusAtividade())
                    .append(", bateria ").append(l.getNivelBateria()).append("%, em ")
                    .append(dataHora(l.getTimestampLeitura())).append('\n');
        } else {
            sb.append("- Última leitura: ").append(SEM_REGISTROS).append('\n');
        }
    }

    private void secaoComedouro(StringBuilder sb, Long petId) {
        sb.append("\nCOMEDOURO — ALIMENTAÇÃO (fonte: leituras IoT do comedouro, até 10 mais recentes)\n");
        List<LeituraComedouro> leituras = leituraComedouroRepository.findTop10ByPetIdOrderByTimestampLeituraDesc(petId);
        if (leituras.isEmpty()) {
            sb.append("- ").append(SEM_REGISTROS).append('\n');
            return;
        }
        LeituraComedouro ultima = leituras.get(0);
        sb.append("- Nível de ração atual: ").append(ultima.getNivelRacaoPct()).append("%\n");
        sb.append("- Consumo na última leitura: ").append(numero(ultima.getPesoConsumidoG())).append(" g\n");

        List<BigDecimal> consumos = leituras.stream()
                .map(LeituraComedouro::getPesoConsumidoG)
                .filter(v -> v != null)
                .toList();
        if (!consumos.isEmpty()) {
            BigDecimal soma = consumos.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal media = soma.divide(BigDecimal.valueOf(consumos.size()), 1, RoundingMode.HALF_UP);
            BigDecimal min = consumos.stream().min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
            BigDecimal max = consumos.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
            sb.append("- Consumo nas ").append(consumos.size()).append(" leituras listadas: média ")
                    .append(numero(media)).append(" g, mínimo ").append(numero(min))
                    .append(" g, máximo ").append(numero(max)).append(" g\n");
        }
        sb.append("- Horário da última leitura: ").append(dataHora(ultima.getTimestampLeitura())).append('\n');
    }

    private void secaoAmbiente(StringBuilder sb, Long petId) {
        sb.append("\nAMBIENTE (fonte: última leitura IoT do sensor de ambiente)\n");
        Optional<LeituraAmbiente> ultima = leituraAmbienteRepository.findTopByPetIdOrderByTimestampLeituraDesc(petId);
        if (ultima.isEmpty()) {
            sb.append("- ").append(SEM_REGISTROS).append('\n');
            return;
        }
        LeituraAmbiente l = ultima.get();
        sb.append("- Temperatura: ").append(numero(l.getTemperaturaAmbiente())).append(" °C\n");
        sb.append("- Umidade: ").append(l.getUmidadePct()).append("%\n");
        sb.append("- Qualidade do ar: ").append(l.getQualidadeArPpm()).append(" ppm\n");
        sb.append("- Pet presente no ambiente: ").append(Boolean.TRUE.equals(l.getPetPresente()) ? "sim" : "não").append('\n');
        sb.append("- Horário: ").append(dataHora(l.getTimestampLeitura())).append('\n');
    }

    private void secaoAlertas(StringBuilder sb, Long petId) {
        sb.append("\nALERTAS ATIVOS (fonte: alertas abertos pelo sistema, não resolvidos)\n");
        List<AlertaSaude> ativos = alertaSaudeRepository.findByPetIdAndResolvidoFalseOrderByDataAlertaDesc(petId);
        if (ativos.isEmpty()) {
            sb.append("- ").append(SEM_REGISTROS).append('\n');
        } else {
            ativos.forEach(a -> sb.append(linhaAlerta(a)));
        }

        sb.append("\nALERTAS RESOLVIDOS RECENTES (fonte: alertas do sistema, até ")
                .append(MAX_ALERTAS_RESOLVIDOS).append(" mais recentes)\n");
        List<AlertaSaude> resolvidos = alertaSaudeRepository.findTop5ByPetIdAndResolvidoTrueOrderByDataAlertaDesc(petId);
        if (resolvidos.isEmpty()) {
            sb.append("- ").append(SEM_REGISTROS).append('\n');
        } else {
            resolvidos.forEach(a -> sb.append(linhaAlerta(a)));
        }
    }

    private String linhaAlerta(AlertaSaude a) {
        return "- " + a.getTipo() + " (nível " + a.getNivel() + "): " + texto(a.getMensagem())
                + " | valor " + numero(a.getValorDetectado()) + " x limite " + numero(a.getLimiteReferencia())
                + " | em " + dataHora(a.getDataAlerta()) + '\n';
    }

    private void secaoConsultas(StringBuilder sb, Long petId, LocalDateTime agora) {
        sb.append("\nCONSULTAS (fonte: consultas registradas pela clínica, até ").append(MAX_CONSULTAS).append(" mais recentes)\n");
        List<Consulta> consultas = consultaRepository.findTop10ByPetIdOrderByDataConsultaDesc(petId);
        if (consultas.isEmpty()) {
            sb.append("- ").append(SEM_REGISTROS).append('\n');
            sb.append("- Dias desde a última consulta: nenhuma consulta registrada\n");
            return;
        }
        consultas.stream().limit(MAX_CONSULTAS).forEach(c -> {
            sb.append("- ").append(dataHora(c.getDataConsulta())).append(" — ").append(c.getTipo());
            if (c.getDataConsulta() != null && c.getDataConsulta().isAfter(agora)) {
                sb.append(" (agendada, ainda não realizada)");
            }
            sb.append(" | diagnóstico: ").append(textoOu(c.getDiagnostico(), "não registrado"));
            sb.append(" | retorno recomendado: ").append(Boolean.TRUE.equals(c.getRetornoRecomendado()) ? "sim" : "não");
            if (c.getDataRetorno() != null) {
                sb.append(" (retorno em ").append(dataHora(c.getDataRetorno())).append(')');
            }
            sb.append('\n');
        });

        // Consulta com data futura é agendamento, não atendimento: conta a
        // partir da última que já aconteceu.
        Optional<Consulta> ultimaRealizada = consultas.stream()
                .filter(c -> c.getDataConsulta() != null && !c.getDataConsulta().isAfter(agora))
                .findFirst();
        if (ultimaRealizada.isPresent()) {
            long dias = Duration.between(ultimaRealizada.get().getDataConsulta(), agora).toDays();
            sb.append("- Dias desde a última consulta realizada: ").append(dias).append('\n');
        } else {
            sb.append("- Dias desde a última consulta realizada: nenhuma consulta realizada ainda (apenas agendamentos)\n");
        }
    }

    private void secaoScore(StringBuilder sb, Long petId) {
        sb.append("\nSCORE DE SAÚDE (fonte: último cálculo salvo pelo sistema — não recalculado aqui)\n");
        Optional<ScoreSaude> atualOpt = scoreSaudeRepository.findTopByPetIdOrderByDataCalculoDesc(petId);
        if (atualOpt.isEmpty()) {
            sb.append("- ").append(SEM_REGISTROS).append('\n');
            return;
        }
        ScoreSaude atual = atualOpt.get();
        sb.append("- Atual: ").append(atual.getScoreTotal()).append("/100 (").append(atual.getCategoria())
                .append("), calculado em ").append(dataHora(atual.getDataCalculo())).append('\n');
        sb.append("- Sub-scores: atividade ").append(atual.getScoreAtividade())
                .append(", alimentação ").append(atual.getScoreAlimentacao())
                .append(", ambiente ").append(atual.getScoreAmbiente())
                .append(", consulta ").append(atual.getScoreConsulta())
                .append(", preventivo ").append(atual.getScorePreventivo()).append('\n');
        sb.append("- Observação do cálculo: ").append(observacao(atual)).append('\n');

        List<ScoreSaude> historico = scoreSaudeRepository.findTop10ByPetIdOrderByDataCalculoDesc(petId);
        sb.append("- Histórico (até 10 cálculos, do mais recente ao mais antigo):\n");
        historico.forEach(s -> sb.append("  ").append(dataHora(s.getDataCalculo())).append(" — ")
                .append(s.getScoreTotal()).append(" (").append(s.getCategoria()).append(") — ")
                .append(observacao(s)).append('\n'));

        if (historico.size() < 2) {
            sb.append("- Tendência: histórico insuficiente (apenas um cálculo)\n");
        } else {
            ScoreSaude maisRecente = historico.get(0);
            ScoreSaude maisAntigo = historico.get(historico.size() - 1);
            int diferenca = maisRecente.getScoreTotal() - maisAntigo.getScoreTotal();
            String tendencia = diferenca > 0 ? "subiu " + diferenca + " pontos"
                    : diferenca < 0 ? "caiu " + (-diferenca) + " pontos"
                    : "estável (0 pontos)";
            sb.append("- Tendência: ").append(tendencia).append(" entre ")
                    .append(dataHora(maisAntigo.getDataCalculo())).append(" e ")
                    .append(dataHora(maisRecente.getDataCalculo())).append('\n');
        }
    }

    private void secaoPreventivo(StringBuilder sb, Long petId) {
        sb.append("\nPLANO PREVENTIVO — PENDÊNCIAS (fonte: eventos preventivos com status PENDENTE)\n");
        LocalDate hoje = LocalDate.now();
        List<EventoPreventivo> pendentes = eventoPreventivoRepository.findByPetIdOrderByDataPrevistaAsc(petId).stream()
                .filter(e -> e.getStatus() == StatusEventoPreventivo.PENDENTE)
                .toList();
        if (pendentes.isEmpty()) {
            sb.append("- ").append(SEM_REGISTROS).append('\n');
            return;
        }
        pendentes.forEach(e -> {
            sb.append("- ").append(e.getTipo()).append(": ").append(texto(e.getDescricao()))
                    .append(" | previsto para ").append(e.getDataPrevista() == null ? "data não informada" : e.getDataPrevista().format(FORMATO_DATA));
            if (e.getDataPrevista() != null && e.getDataPrevista().isBefore(hoje)) {
                sb.append(" | ATRASADO há ").append(ChronoUnit.DAYS.between(e.getDataPrevista(), hoje)).append(" dia(s)");
            }
            sb.append('\n');
        });
    }

    // ---------------------------------------------------------- formatação

    private String observacao(ScoreSaude s) {
        return textoOu(s.getObservacao(), "sem observação registrada");
    }

    private String idade(LocalDate nascimento) {
        if (nascimento == null) {
            return "não informada";
        }
        Period p = Period.between(nascimento, LocalDate.now());
        return p.getYears() + " ano(s) e " + p.getMonths() + " mês(es)";
    }

    private String dataHora(LocalDateTime data) {
        return data == null ? "data não informada" : data.format(FORMATO_DATA_HORA);
    }

    private String numero(BigDecimal valor) {
        return valor == null ? "não informado" : valor.stripTrailingZeros().toPlainString();
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.strip();
    }

    private String textoOu(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor.strip();
    }
}
