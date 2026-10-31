package fiap.com.br.petcarehub.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record StatusConhecimentoResponse(
        long totalTrechos,
        List<String> fontes,
        LocalDateTime ultimaIndexacao,
        String modeloEmbedding
) {
}
