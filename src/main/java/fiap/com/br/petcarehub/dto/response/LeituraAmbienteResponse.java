package fiap.com.br.petcarehub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraAmbienteResponse(
        Long id,
        Long petId,
        String petNome,
        BigDecimal temperaturaAmbiente,
        Integer umidadePct,
        Integer qualidadeArPpm,
        Boolean petPresente,
        LocalDateTime timestampLeitura
) {}
