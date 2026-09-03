package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClinicaRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 8, max = 100) String senha,
        @NotBlank @Size(max = 18) String cnpj,
        @Size(max = 200) String endereco,
        @Size(max = 20) String telefone
) {}
