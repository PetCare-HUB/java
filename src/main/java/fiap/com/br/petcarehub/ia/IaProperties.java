package fiap.com.br.petcarehub.ia;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Parâmetros da IA do PetCare Hub (prefixo {@code app.ia} no application.properties).
 *
 * <p>Os defaults abaixo valem quando a propriedade não está configurada, para a
 * aplicação subir mesmo com um application.properties antigo.</p>
 *
 * @param rag               parâmetros da recuperação na base de conhecimento
 * @param pergunta          limites da pergunta livre do tutor
 * @param indexarAoIniciar  se true, reindexa os .md da base na subida
 * @param pastaConhecimento padrão de recursos com os .md da base
 * @param timeoutMs         timeout, em milissegundos, de cada chamada ao Gemini
 * @param tentativas        tentativas do SDK do Google por chamada (429/5xx/rede), dentro do timeout
 *                          (chat e embeddings); aplicado no Client do SDK em config/IaConfig
 */
@ConfigurationProperties(prefix = "app.ia")
public record IaProperties(
        @DefaultValue Rag rag,
        @DefaultValue Pergunta pergunta,
        @DefaultValue("false") boolean indexarAoIniciar,
        @DefaultValue("classpath:conhecimento/*.md") String pastaConhecimento,
        @DefaultValue("60000") int timeoutMs,
        @DefaultValue("2") int tentativas
) {

    /**
     * @param topK                quantos trechos da base entram no prompt
     * @param similarityThreshold similaridade mínima (cosseno, 0 a 1) para um trecho ser usado
     */
    public record Rag(
            @DefaultValue("4") int topK,
            @DefaultValue("0.45") double similarityThreshold
    ) {}

    /**
     * @param maxCaracteres       tamanho máximo da pergunta do tutor
     * @param memoriaMaxMensagens janela de mensagens guardadas por conversa
     */
    public record Pergunta(
            @DefaultValue("500") int maxCaracteres,
            @DefaultValue("10") int memoriaMaxMensagens
    ) {}
}
