package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.response.StatusConhecimentoResponse;
import fiap.com.br.petcarehub.dto.response.TrechoConhecimentoResponse;
import fiap.com.br.petcarehub.ia.ConhecimentoVectorStore;
import fiap.com.br.petcarehub.ia.IaProperties;
import fiap.com.br.petcarehub.repository.DocumentoConhecimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base de conhecimento veterinária usada no RAG: indexa os .md de
 * src/main/resources/conhecimento e permite consultá-la direto (diagnóstico
 * do RAG pelo admin, sem passar pelo modelo de chat).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseConhecimentoService {

    private static final int TOP_K_MAXIMO = 20;

    private final ConhecimentoVectorStore vectorStore;
    private final DocumentoConhecimentoRepository repository;
    private final IaProperties props;

    /**
     * Relê todos os .md, quebra em trechos, gera os embeddings e substitui a base inteira.
     *
     * <p>Transacional de propósito: se o Gemini falhar no meio, o delete é desfeito
     * e a base antiga continua valendo, em vez de ficar vazia. Obrigatório rodar
     * depois de trocar o modelo ou a dimensão do embedding: trechos gravados com
     * outro modelo são ignorados na busca.</p>
     */
    @Transactional
    public StatusConhecimentoResponse reindexar() {
        Resource[] arquivos = localizarArquivos();
        if (arquivos.length == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Nenhum arquivo da base de conhecimento encontrado em " + props.pastaConhecimento());
        }

        TokenTextSplitter splitter = TokenTextSplitter.builder().build();
        List<Document> trechos = new ArrayList<>();
        for (Resource arquivo : arquivos) {
            trechos.addAll(splitter.apply(lerArquivo(arquivo)));
        }

        repository.deleteAllInBatch();
        vectorStore.accept(trechos);

        log.info("Base de conhecimento reindexada: {} arquivo(s), {} trecho(s).", arquivos.length, trechos.size());
        return status();
    }

    @Transactional(readOnly = true)
    public StatusConhecimentoResponse status() {
        List<String> modelos = repository.findModelosEmbedding();
        String modelo = modelos.isEmpty() ? vectorStore.getModeloEmbedding() : String.join(", ", modelos);
        return new StatusConhecimentoResponse(
                repository.count(),
                repository.findFontesDistintas(),
                repository.findUltimaIndexacao().orElse(null),
                modelo
        );
    }

    /** Busca semântica direta na base, com o mesmo limiar de similaridade usado no RAG. */
    // Sem @Transactional de propósito: o embedding da consulta chama o Gemini e não
    // deve segurar conexão do pool; o próprio VectorStore abre uma transação curta
    // só para ler os CLOBs.
    public List<TrechoConhecimentoResponse> buscar(String q, int topK) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o texto da busca (parâmetro q).");
        }
        if (topK < 1 || topK > TOP_K_MAXIMO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "topK deve estar entre 1 e " + TOP_K_MAXIMO + ".");
        }

        List<Document> documentos = vectorStore.similaritySearch(SearchRequest.builder()
                .query(q)
                .topK(topK)
                .similarityThreshold(props.rag().similarityThreshold())
                .build());

        return documentos.stream()
                .map(d -> new TrechoConhecimentoResponse(
                        texto(d.getMetadata().get(ConhecimentoVectorStore.META_FONTE)),
                        texto(d.getMetadata().get(ConhecimentoVectorStore.META_TITULO)),
                        d.getText(),
                        d.getScore()))
                .toList();
    }

    private Resource[] localizarArquivos() {
        try {
            return new PathMatchingResourcePatternResolver().getResources(props.pastaConhecimento());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível ler a pasta da base de conhecimento.", e);
        }
    }

    /**
     * Lê um .md em documentos por seção. O MarkdownDocumentReader guarda o heading
     * da seção só na metadata "title" (fora do texto), então o heading é
     * recolocado no início do texto - assim ele também entra no embedding e o
     * trecho recuperado isoladamente continua dizendo do que trata.
     */
    private List<Document> lerArquivo(Resource arquivo) {
        String nomeArquivo = arquivo.getFilename() == null ? "desconhecido.md" : arquivo.getFilename();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(arquivo, MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .withAdditionalMetadata(ConhecimentoVectorStore.META_FONTE, nomeArquivo)
                .build());

        List<Document> secoes = reader.get();

        // Título do arquivo = primeiro heading; sem heading, o nome do arquivo.
        String tituloArquivo = secoes.stream()
                .map(d -> d.getMetadata().get("title"))
                .filter(t -> t != null && !t.toString().isBlank())
                .map(Object::toString)
                .findFirst()
                .orElse(nomeArquivo);

        List<Document> resultado = new ArrayList<>();
        for (Document secao : secoes) {
            Object tituloSecao = secao.getMetadata().get("title");
            String titulo = tituloSecao == null || tituloSecao.toString().equals(tituloArquivo)
                    ? tituloArquivo
                    : tituloArquivo + " - " + tituloSecao;

            Map<String, Object> metadata = new HashMap<>(secao.getMetadata());
            metadata.put(ConhecimentoVectorStore.META_FONTE, nomeArquivo);
            metadata.put(ConhecimentoVectorStore.META_TITULO, titulo);

            String texto = tituloSecao == null ? secao.getText() : tituloSecao + "\n\n" + secao.getText();
            resultado.add(Document.builder().text(texto).metadata(metadata).build());
        }
        return resultado;
    }

    private static String texto(Object valor) {
        return valor == null ? null : valor.toString();
    }
}
