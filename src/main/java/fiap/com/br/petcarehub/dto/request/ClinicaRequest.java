package fiap.com.br.petcarehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClinicaRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 18) String cnpj,
        @Size(max = 200) String endereco,
        @Size(max = 20) String telefone
) {}
