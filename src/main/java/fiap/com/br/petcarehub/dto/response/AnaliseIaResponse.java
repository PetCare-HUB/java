package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.TipoAnaliseIa;

import java.time.LocalDateTime;
import java.util.List;

public record AnaliseIaResponse(
        Long id,
        Long petId,
        String petNome,
        TipoAnaliseIa tipo,
        String pergunta,
        String resposta,
        Long scoreId,
        Long alertaId,
        String modelo,
        List<String> fontes,
        LocalDateTime dataAnalise
) {}
