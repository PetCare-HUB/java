package fiap.com.br.petcarehub.dto.request;

import fiap.com.br.petcarehub.enums.StatusAtividade;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record LeituraColeiraRequest(
        @NotNull Long petId,
        @NotNull StatusAtividade statusAtividade,
        @NotNull @Min(0) @Max(100) Integer nivelBateria,
        LocalDateTime timestampLeitura
) {}
