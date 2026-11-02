package fiap.com.br.petcarehub.enums;

public enum TipoAlerta {
    BATERIA_COLEIRA_BAIXA("Bateria da coleira baixa"),
    RACAO_BAIXA("Ração baixa"),
    BAIXA_ALIMENTACAO("Baixa alimentação"),
    AMBIENTE_RUIM("Ambiente inadequado"),
    TEMPERATURA_FORA_DA_FAIXA("Temperatura fora da faixa ideal"),
    UMIDADE_FORA_DA_FAIXA("Umidade fora da faixa ideal"),
    SCORE_CRITICO("Score de saúde crítico"),
    CONSULTA_ATRASADA("Consulta atrasada"),
    SCORE_BAIXO("Score de saúde baixo"),
    ATIVIDADE_BAIXA("Atividade baixa");

    /**
     * Como o alerta se chama em português. Existe porque o contexto da IA é
     * lido pelo modelo e repetido para o tutor: sem isso ele escrevia
     * "BAIXA_ALIMENTACAO" no meio da resposta. É o mesmo texto do TIPO_LABEL
     * do app mobile.
     *
     * <p>Não muda a serialização: o JSON continua levando o nome da constante,
     * que é o que o app espera.</p>
     */
    private final String descricao;

    TipoAlerta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
