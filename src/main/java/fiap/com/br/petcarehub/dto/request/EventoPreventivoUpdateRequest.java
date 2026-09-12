package fiap.com.br.petcarehub.dto.request;

import fiap.com.br.petcarehub.enums.TipoEventoPreventivo;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EventoPreventivoUpdateRequest(
        @NotNull TipoEventoPreventivo tipo,
        @NotBlank @Size(max = 300) String descricao,
        @NotNull @FutureOrPresent LocalDate dataPrevista
) {}
