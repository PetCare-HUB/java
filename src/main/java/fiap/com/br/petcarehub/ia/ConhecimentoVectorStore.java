package fiap.com.br.petcarehub.ia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.errors.ApiException;
import com.google.genai.errors.GenAiIOException;
import fiap.com.br.petcarehub.entity.DocumentoConhecimento;
import fiap.com.br.petcarehub.repository.DocumentoConhecimentoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * VectorStore do PetCare Hub, persistido na tabela DOCUMENTO_CONHECIMENTO.
 *
 * <p>Por que uma implementação própria: o Oracle 19c não tem tipo VECTOR, então o
 * {@code spring-ai-starter-vector-store-oracle} não serve. O embedding vira JSON
 * num CLOB e a similaridade por cosseno é calculada em Java. A base é uma
 * coleção pequena de .md (dezenas de trechos), então carregar tudo a cada busca
 * custa pouco e evita depender de extensão do banco.</p>
 *
 * <p>É o único bean VectorStore da aplicação: o QuestionAnswerAdvisor (config/IaConfig)
 * e o BaseConhecimentoService usam este.</p>
 */
@Slf4j
@Component
public class ConhecimentoVectorStore implements VectorStore {

    public static final String META_FONTE = "fonte";
    public static final String META_TITULO = "titulo";

    private static final String MENSAGEM_INDISPONIVEL =
            "Serviço de IA indisponível no momento. Tente novamente em instantes.";

    private static final TypeReference<Map<String, Object>> TIPO_METADADOS = new TypeReference<>() {};

    private final EmbeddingModel embeddingModel;
    private final DocumentoConhecimentoRepository repository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final String modeloEmbedding;

    /** Limite da Gemini API por requisição de embedding em lote (batchEmbedContents). */
    private static final int TAMANHO_LOTE_EMBEDDING = 100;

    public ConhecimentoVectorStore(EmbeddingModel embeddingModel,
                                   DocumentoConhecimentoRepository repository,
                                   ObjectMapper objectMapper,
                                   TransactionTemplate transactionTemplate,
                                   @Value("${spring.ai.google.genai.embedding.text.options.model:desconhecido}") String modelo) {
        this.embeddingModel = embeddingModel;
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.transactionTemplate = transactionTemplate;
        // Gravado em cada trecho: se o modelo de embedding mudar, os vetores
        // antigos deixam de ser comparáveis e a base precisa ser reindexada.
        this.modeloEmbedding = "google-genai/" + modelo;
    }

    /** Nome do modelo de embedding configurado (o mesmo gravado em MODELO_EMBEDDING). */
    public String getModeloEmbedding() {
        return modeloEmbedding;
    }

    @Override
    @Transactional
    public void add(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        // Embeda tudo antes de tocar no banco, em lotes de até 100 textos (limite da
        // Gemini API por requisição).
        List<String> textos = documents.stream().map(Document::getText).toList();
        List<float[]> vetores = new ArrayList<>(textos.size());
        for (int inicio = 0; inicio < textos.size(); inicio += TAMANHO_LOTE_EMBEDDING) {
            List<String> lote = textos.subList(inicio, Math.min(inicio + TAMANHO_LOTE_EMBEDDING, textos.size()));
            vetores.addAll(chamarProvedor(() -> embeddingModel.embed(lote)));
        }

        Map<String, DocumentoConhecimento> existentes = repository
                .findByDocIdIn(documents.stream().map(Document::getId).toList())
                .stream()
                .collect(Collectors.toMap(DocumentoConhecimento::getDocId, Function.identity()));

        List<DocumentoConhecimento> paraSalvar = new ArrayList<>();
        for (int i = 0; i < documents.size(); i++) {
            Document documento = documents.get(i);
            float[] vetor = vetores.get(i);
            Map<String, Object> metadata = documento.getMetadata();

            // Upsert por docId: o mesmo Document adicionado de novo atualiza a linha.
            DocumentoConhecimento entidade = existentes.getOrDefault(documento.getId(), new DocumentoConhecimento());
            entidade.setDocId(documento.getId());
            entidade.setFonte(limitar(textoOu(metadata.get(META_FONTE), "desconhecida"), 200));
            entidade.setTitulo(limitar(textoOu(metadata.get(META_TITULO), null), 200));
            entidade.setConteudo(documento.getText());
            entidade.setMetadados(paraJson(metadata));
            entidade.setEmbedding(paraJson(vetor));
            entidade.setModeloEmbedding(modeloEmbedding);
            entidade.setDimensoes(vetor.length);
            paraSalvar.add(entidade);
        }

        repository.saveAll(paraSalvar);
        log.info("{} trecho(s) da base de conhecimento gravados com embedding {} ({} dimensões).",
                paraSalvar.size(), modeloEmbedding, vetores.isEmpty() ? 0 : vetores.get(0).length);
    }

    @Override
    @Transactional
    public void delete(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            return;
        }
        repository.deleteByDocIdIn(idList);
    }

    @Override
    public void delete(Filter.Expression filterExpression) {
        throw new UnsupportedOperationException(
                "ConhecimentoVectorStore não suporta exclusão por filtro; use delete(List<String>) com os ids.");
    }

    /**
     * Busca por similaridade de cosseno. Respeita {@code topK} e
     * {@code similarityThreshold} do SearchRequest; filtros de metadata não são
     * suportados (a base não precisa deles) e são ignorados com aviso.
     */
    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        if (request.hasFilterExpression()) {
            log.warn("Filtro de metadata ignorado pelo ConhecimentoVectorStore: {}", request.getFilterExpression());
        }

        String query = request.getQuery();
        if (query == null || query.isBlank()) {
            return List.of();
        }

        // Base vazia (ainda não indexada): não gasta uma chamada ao Gemini para
        // comparar com nada — e a IA continua funcionando só com os dados do pet.
        if (repository.count() == 0) {
            log.warn("Base de conhecimento vazia: nenhum trecho para o RAG. Rode POST /admin/ia/reindexar.");
            return List.of();
        }

        // Sem transação aberta aqui: a chamada ao Gemini pode demorar e não deve
        // segurar uma conexão do pool. Só a leitura dos CLOBs abre uma transação
        // curta (no Oracle o locator deixa de valer fora dela).
        float[] vetorConsulta = chamarProvedor(() -> embeddingModel.embed(query));

        List<Document> resultado = Objects.requireNonNullElse(
                transactionTemplate.execute(status -> compararComBase(vetorConsulta)), List.of());

        return resultado.stream()
                .filter(d -> d.getScore() >= request.getSimilarityThreshold())
                .sorted(Comparator.comparing(Document::getScore).reversed())
                .limit(request.getTopK())
                .toList();
    }

    /** Lê todos os trechos (CLOBs) dentro da transação e calcula o cosseno com a consulta. */
    private List<Document> compararComBase(float[] vetorConsulta) {
        List<Document> resultado = new ArrayList<>();
        boolean avisouModeloDiferente = false;
        for (DocumentoConhecimento trecho : repository.findAll()) {
            if (!modeloEmbedding.equals(trecho.getModeloEmbedding())) {
                // Trecho indexado com outro modelo (ex.: antes da troca de provedor):
                // vetores de modelos diferentes não são comparáveis, mesmo que a
                // dimensão coincida. Avisa uma vez por busca e pula o trecho.
                if (!avisouModeloDiferente) {
                    log.warn("Base de conhecimento indexada com outro modelo de embedding ({}) e a consulta usa {}"
                                    + " - trechos ignorados. Reindexe a base por POST /admin/ia/reindexar.",
                            trecho.getModeloEmbedding(), modeloEmbedding);
                    avisouModeloDiferente = true;
                }
                continue;
            }
            float[] vetor = deJson(trecho.getEmbedding());
            if (vetor.length != vetorConsulta.length) {
                // Trecho indexado com outro modelo: não é comparável.
                log.warn("Trecho {} tem {} dimensões e a consulta {} - reindexe a base.",
                        trecho.getDocId(), vetor.length, vetorConsulta.length);
                continue;
            }
            double similaridade = cosseno(vetorConsulta, vetor);
            resultado.add(paraDocument(trecho, similaridade));
        }
        return resultado;
    }

    private Document paraDocument(DocumentoConhecimento trecho, double similaridade) {
        Map<String, Object> metadata = new HashMap<>(deJsonMetadados(trecho.getMetadados()));
        metadata.put(META_FONTE, trecho.getFonte());
        if (trecho.getTitulo() != null) {
            metadata.put(META_TITULO, trecho.getTitulo());
        }
        return Document.builder()
                .id(trecho.getDocId())
                .text(trecho.getConteudo())
                .metadata(metadata)
                .score(similaridade)
                .build();
    }

    static double cosseno(float[] a, float[] b) {
        double produto = 0;
        double normaA = 0;
        double normaB = 0;
        for (int i = 0; i < a.length; i++) {
            produto += a[i] * b[i];
            normaA += a[i] * a[i];
            normaB += b[i] * b[i];
        }
        if (normaA == 0 || normaB == 0) {
            return 0;
        }
        return produto / (Math.sqrt(normaA) * Math.sqrt(normaB));
    }

    /**
     * Chamada ao provedor de embedding com o tratamento combinado para a IA:
     * falha de rede ou do Gemini vira 503, com a causa no log. ApiException (4xx/5xx)
     * e GenAiIOException (rede/timeout) vêm do SDK do Google, que não as embrulha
     * nas exceções do Spring AI.
     */
    private <T> T chamarProvedor(Supplier<T> chamada) {
        try {
            return chamada.get();
        } catch (TransientAiException | NonTransientAiException | ApiException | GenAiIOException ex) {
            log.warn("Falha ao gerar embedding ({}): {}", modeloEmbedding, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, MENSAGEM_INDISPONIVEL, ex);
        }
    }

    private String paraJson(Object valor) {
        try {
            return objectMapper.writeValueAsString(valor);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Não foi possível serializar dado da base de conhecimento.", e);
        }
    }

    private float[] deJson(String json) {
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Embedding inválido gravado na base de conhecimento.", e);
        }
    }

    private Map<String, Object> deJsonMetadados(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, TIPO_METADADOS);
        } catch (JsonProcessingException e) {
            log.warn("Metadados inválidos na base de conhecimento, ignorando: {}", e.getMessage());
            return Map.of();
        }
    }

    private static String textoOu(Object valor, String padrao) {
        return valor == null || valor.toString().isBlank() ? padrao : valor.toString();
    }

    private static String limitar(String texto, int max) {
        return texto == null || texto.length() <= max ? texto : texto.substring(0, max);
    }
}
