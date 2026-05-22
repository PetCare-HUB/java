package fiap.com.br.petcarehub.dto.request;

import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertaSaudeRequest(
        @NotNull Long petId,
        @NotNull TipoAlerta tipo,
        @NotNull NivelAlerta nivel,
        @NotBlank @Size(max = 500) String mensagem
) {}
