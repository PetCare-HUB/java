package fiap.com.br.petcarehub.dto.response;

public record ClinicaResponse(
        Long id,
        String nome,
        String email,
        String cnpj,
        String endereco,
        String telefone
) {}
