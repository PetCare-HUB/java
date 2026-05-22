package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResponsavelRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 20) String telefone,
        @NotBlank @Size(min = 11, max = 14) String cpf
) {}
