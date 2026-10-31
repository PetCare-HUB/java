package fiap.com.br.petcarehub.dto.response;

public record TrechoConhecimentoResponse(
        String fonte,
        String titulo,
        String conteudo,
        Double similaridade
) {
}
