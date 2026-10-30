package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.StatusAcesso;

public record ClinicaResponse(
        Long id,
        String nome,
        String email,
        String cnpj,
        String endereco,
        String telefone,
        char ativo,
        StatusAcesso statusAcesso
) {}
