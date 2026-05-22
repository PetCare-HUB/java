package fiap.com.br.petcarehub.dto.response;

import java.time.LocalDateTime;

public record ResponsavelResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        LocalDateTime dataCadastro
) {}
