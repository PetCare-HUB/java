package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String Username, @NotBlank String password) {
}
