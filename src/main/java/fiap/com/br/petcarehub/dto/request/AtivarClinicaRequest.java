package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtivarClinicaRequest(
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 18) String cnpj,
        @NotBlank @Size(min = 16, max = 200) String codigoAtivacao,
        @NotBlank @Size(min = 8, max = 100) String senha
) {}
