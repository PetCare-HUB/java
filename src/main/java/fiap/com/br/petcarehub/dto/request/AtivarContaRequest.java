package fiap.com.br.petcarehub.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtivarContaRequest(

        @NotBlank
        @Size(max = 120)
        String nome,

        @NotBlank
        @Size(min = 11, max = 14)
        String cpf,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String senha
) {
}