package fiap.com.br.petcarehub.dto.request;

import fiap.com.br.petcarehub.enums.TipoConsulta;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaRequest(
        @NotNull Long petId,
        @NotNull Long clinicaId,
        @NotNull @FutureOrPresent LocalDateTime dataConsulta,
        @NotNull TipoConsulta tipo,
        @Size(max = 500) String descricao,
        @Size(max = 500) String diagnostico,
        @NotNull Boolean retornoRecomendado,
        LocalDateTime dataRetorno,
        @NotNull @PositiveOrZero BigDecimal valor
) {}
