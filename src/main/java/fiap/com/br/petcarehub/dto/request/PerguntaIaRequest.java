package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerguntaIaRequest(
        @NotBlank @Size(max = 500) String pergunta
) {}
