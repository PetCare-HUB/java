package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraAmbienteRequest(
        @NotNull Long petId,
        @NotNull BigDecimal temperaturaAmbiente,
        @NotNull @Min(0) @Max(100) Integer umidadePct,
        @NotNull @Min(0) Integer qualidadeArPpm,
        @NotNull Boolean petPresente,
        LocalDateTime timestampLeitura
) {}
