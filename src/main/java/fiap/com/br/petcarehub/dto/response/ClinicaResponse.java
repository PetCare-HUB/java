package fiap.com.br.petcarehub.dto.response;

public record ClinicaResponse(
        Long id,
        String nome,
        String cnpj,
        String endereco,
        String telefone
) {}
