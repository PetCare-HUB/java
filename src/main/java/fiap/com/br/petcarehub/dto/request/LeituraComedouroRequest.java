package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraComedouroRequest(
        @NotNull Long petId,
        @NotNull @Min(0) @Max(100) Integer nivelRacaoPct,
        @NotNull @PositiveOrZero BigDecimal pesoConsumidoG,
        LocalDateTime timestampLeitura
) {}
